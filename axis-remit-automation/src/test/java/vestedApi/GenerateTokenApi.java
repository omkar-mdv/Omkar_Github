package vestedApi;

import base.ApiBaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Listeners(listeners.TestListener.class)
public class GenerateTokenApi extends ApiBaseTest {

	public static String generatedToken;

	private Response hitGenerateTokenApi(Map<String, Object> requestBody) {
		Response response = given().spec(requestSpec).body(requestBody).when()
				.post("/services/api/partner/generateToken").then().log().all().extract().response();

		listeners.TestListener.test.get().info("Request Body: " + requestBody);
		listeners.TestListener.test.get().info("Response Body: " + response.asPrettyString());

		return response;
	}

	private void validateSuccessResponse(Response response) {
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));
	}

	private void validateFailureResponse(Response response, String errCode, String messageField, String messageValue) {
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), errCode);
		Assert.assertEquals(response.jsonPath().getString(messageField), messageValue);
		Assert.assertEquals(response.jsonPath().getString("token"), "");
		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	private Map<String, Object> getDefaultRequestBody() {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "REQTEST002");
		requestBody.put("crn", "100759017");
		requestBody.put("accNo", "7150157172");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");
		return requestBody;
	}
	
	//1
	@Test(priority = 1)
	public void verifyGenerateTokenApiWithValidCRNAndAccountNumberForEligibleETBCustomer() {
		Map<String, Object> requestBody = getDefaultRequestBody();

		Response response = hitGenerateTokenApi(requestBody);
		validateSuccessResponse(response);

		generatedToken = response.jsonPath().getString("token");

		Assert.assertNotNull(generatedToken);
		Assert.assertFalse(generatedToken.isEmpty());
	}

	//2
	@Test
	public void verifyGenerateTokenApiWithEmptyCRN() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR1008", "errorDescription", "Technical Decline");
	}

	//3
	@Test
	public void verifyGenerateTokenApiWhenAadharLinkIsN() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "27374059");
		requestBody.put("accNo", "04221040003886");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR7004", "errorDescription", "Business Decline");
	}

	//4
	@Test
	public void verifyGenerateTokenApiWithMissingMandatoryFieldAccNo() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("accNo", "");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR1008", "errorDescription", "Technical Decline");
	}

	//5
	@Test
	public void verifyGenerateTokenApiWithMissingClientCode() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("clientCode", "");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR1008", "errorDescription", "Technical Decline");
	}

	//6
	@Test
	public void verifyGenerateTokenApiWithCRNAboveMaximumLength() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "1234567890123456");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR1008", "errorDescription", "Technical Decline");
	}

	//7
	@Test
	public void verifyGenerateTokenApiWithAlphabeticCharactersInCRN() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "ABCD59017");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR1008", "errorDescription", "Technical Decline");
	}

	//8
	@Test
	public void verifyGenerateTokenApiWithSpecialCharactersInAccNo() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("accNo", "0958!@#$%");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR1008", "errorDescription", "Technical Decline");
	}

	//9
	@Test
	public void verifyGenerateTokenApiValidatesCustomerViaCRNApiAndAccountInquiryApi() {
		Map<String, Object> requestBody = getDefaultRequestBody();

		Response response = hitGenerateTokenApi(requestBody);
		validateSuccessResponse(response);

		String token = response.jsonPath().getString("token");
		Assert.assertNotNull(token);
		Assert.assertFalse(token.isEmpty());
	}

	//10
	@Test
	public void verifyGenerateTokenApiWhenCustomerCRNStatusIsBlacklisted() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "8593560");
		requestBody.put("accNo", "08160140009332");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR7002", "errorDescription", "Business Decline");
	}

	//11
	@Test
	public void verifyGenerateTokenApiWithCRNBelowMinimumLength() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "1234567");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR1008", "errorDescription", "Technical Decline");
	}

	//12
	@Test
	public void verifyGenerateTokenApiWithInvalidNonExistentCRN() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "99999999");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR7002", "message", "Business Decline");
	}
}