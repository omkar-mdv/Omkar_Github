package vestedApi;

import io.restassured.response.Response;
import listeners.TestListener;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import base.ApiBaseTest;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Listeners(TestListener.class)
public class OtpApi extends ApiBaseTest {

	public static String generatedToken;
	public static String requestId; // Dynamic requestId

	private Map<String, Object> getGenerateTokenRequest() {

		requestId = "REQ" + System.currentTimeMillis();

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", requestId);
		requestBody.put("crn", "100759017");
		requestBody.put("accNo", "7150157172");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");

		return requestBody;
	}

	private Map<String, Object> getOtpRequest() {

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", requestId); // same dynamic requestId
		requestBody.put("otpType", "SMS");
		requestBody.put("crn", "100759017");
		requestBody.put("accNo", "7150157172");
		requestBody.put("panNo", "");
		requestBody.put("channel", "WEB");
		requestBody.put("groupId", "KB");
		requestBody.put("clientCode", "VESTED");

		return requestBody;
	}

	private void logToReport(Map<String, Object> requestBody, Response response) {
		if (TestListener.test.get() != null) {
			TestListener.test.get().info("Request Body: " + requestBody);
			TestListener.test.get().info("Response Body: " + response.asPrettyString());
		}
	}

	// 1
	@Test
	public void verifyGenerateTokenApiWithValidCRNAndAccountNumber() {

		Map<String, Object> requestBody = getGenerateTokenRequest();

		Response response = given().spec(requestSpec).body(requestBody).when()
				.post("/services/api/partner/generateToken").then().extract().response();

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));

		generatedToken = response.jsonPath().getString("token");
		Assert.assertNotNull(generatedToken);
		Assert.assertFalse(generatedToken.isEmpty());
	}

	// 2
	@Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber")
	public void verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber() {

		Map<String, Object> requestBody = getOtpRequest();

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody).when()
				.post("/services/api/partner/sendOTP").then().extract().response();

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));
	}

	// 3
	@Test(dependsOnMethods = "verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber")
	public void verifyOtpApiWithIncorrectOtp() {

		Map<String, Object> requestBody = getOtpRequest();
		requestBody.put("otp", "999999");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody).when()
				.post("/services/api/partner/verifyOTP").then().extract().response();

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1009");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "OTP is incorrect");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	// 4
//	@Test(dependsOnMethods = "verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber")
//	public void verifyOtpApiWithExpiredOtpAfterFiveMinutes() throws InterruptedException {
//
//		// Wait for OTP expiry (6 minutes)
//		Thread.sleep(360000); // 360000 ms = 6 minutes
//
//		Map<String, Object> requestBody = getOtpRequest();
//		requestBody.put("otp", "123456");
//
//		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody).when()
//				.post("/services/api/partner/verifyOTP").then().extract().response();
//
//		logToReport(requestBody, response);
//
//		Assert.assertEquals(response.getStatusCode(), 200);
//		Assert.assertEquals(response.jsonPath().getString("status"), "F");
//
//		// Validate error code
//		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1010");
//
//		// Validate errorDescription
//		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "OTP is expired");
//
//		Assert.assertFalse(response.jsonPath().getBoolean("success"));
//		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
//	}

	// 5
	@Test(dependsOnMethods = "verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber")
	public void verifyOtpApiWithCorrectSixDigitOtp() {

		Map<String, Object> requestBody = getOtpRequest();
		requestBody.put("otp", "123456");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody).when()
				.post("/services/api/partner/verifyOTP").then().extract().response();

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "S");
	}

	// 6
	@Test(dependsOnMethods = "verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber")
	public void verifyOtpApiWithLessThanSixDigits() {

		Map<String, Object> requestBody = getOtpRequest();
		requestBody.put("otp", "12345");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody).when()
				.post("/services/api/partner/verifyOTP").then().extract().response();

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");

		// Validate errorDescription
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	// 7
	@Test(dependsOnMethods = "verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber")
	public void verifyOtpApiWithMoreThanSixDigits() {

		Map<String, Object> requestBody = getOtpRequest();
		requestBody.put("otp", "1234567");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody).when()
				.post("/services/api/partner/verifyOTP").then().extract().response();

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");

		// Validate errorDescription
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	// 8
	@Test(dependsOnMethods = "verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber")
	public void verifyOtpApiWithAlphabeticCharactersInOtpField() {

		Map<String, Object> requestBody = getOtpRequest();
		requestBody.put("otp", "ABCDEF");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody).when()
				.post("/services/api/partner/verifyOTP").then().extract().response();

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");

		// Validate errorDescription
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	// 9
	@Test(dependsOnMethods = "verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber")
	public void verifyOtpApiWithoutAuthToken() {

		Map<String, Object> requestBody = getOtpRequest();
		requestBody.put("otp", "123456");

		Response response = given().spec(requestSpec).body(requestBody) // No authToken header
				.when().post("/services/api/partner/verifyOTP").then().extract().response();

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1007");

		// Validate errorDescription
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical error");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	// 10
	@Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber")
	public void verifySendOtpApiWithMissingMandatoryCRNField() {

		Map<String, Object> requestBody = getOtpRequest();
		requestBody.put("crn", ""); // Missing mandatory CRN

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody).when()
				.post("/services/api/partner/sendOTP").then().extract().response();

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");

		// Validate errorDescription
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	// 11
	@Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber")
	public void verifySendOtpApiWithInvalidExpiredAuthToken() {

		Map<String, Object> requestBody = getOtpRequest();

		Response response = given().spec(requestSpec).header("authToken", "INVALID_EXPIRED_TOKEN").body(requestBody)
				.when().post("/services/api/partner/sendOTP").then().extract().response();

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1007");

		// Validate errorDescription
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical error");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	// 12
	@Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber")
	public void verifySendOtpApiWithoutAuthTokenInHeader() {

		Map<String, Object> requestBody = getOtpRequest();

		Response response = given().spec(requestSpec).body(requestBody) // No authToken header
				.when().post("/services/api/partner/sendOTP").then().extract().response();

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1007");

		// Validate errorDescription
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical error");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}
}