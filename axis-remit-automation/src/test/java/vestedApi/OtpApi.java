package vestedApi;

import base.ApiBaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OtpApi extends ApiBaseTest {

	public static String generatedToken;

	@Test(priority = 1)
	public void verifyGenerateTokenApiWithValidCRNAndAccountNumber() {

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "REQTEST002");
		requestBody.put("crn", "100759017");
		requestBody.put("accNo", "7150157172");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");

		Response response = given().spec(requestSpec).body(requestBody).when()
				.post("/services/api/partner/generateToken").then().log().all().extract().response();

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "S");

		generatedToken = response.jsonPath().getString("token");

		Assert.assertNotNull(generatedToken, "Generated token should not be null");
		Assert.assertFalse(generatedToken.isEmpty(), "Generated token should not be empty");

		System.out.println("Generated Token: " + generatedToken);
	}

	@Test(priority = 2, dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber")
	public void verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber() {

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "REQTEST002");
		requestBody.put("otpType", "SMS");
		requestBody.put("crn", "100759017");
		requestBody.put("accNo", "7150157172");
		requestBody.put("panNo", "");
		requestBody.put("channel", "WEB");
		requestBody.put("groupId", "KB");
		requestBody.put("clientCode", "VESTED");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody).when()
				.post("/services/api/partner/sendOTP").then().log().all().extract().response();

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));
	}

	@Test(priority = 3, dependsOnMethods = "verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber")
	public void verifyOtpApiWithCorrectSixDigitOtp() {

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "REQTEST002");
		requestBody.put("otpType", "SMS");
		requestBody.put("crn", "100759017");
		requestBody.put("accNo", "7150157172");
		requestBody.put("panNo", "");
		requestBody.put("channel", "WEB");
		requestBody.put("groupId", "KB");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("otp", "123456"); // Replace with actual OTP

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody).when()
				.post("/services/api/partner/verifyOTP").then().log().all().extract().response();

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));
	}
}