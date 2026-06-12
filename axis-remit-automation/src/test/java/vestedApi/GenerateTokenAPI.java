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
public class GenerateTokenAPI extends ApiBaseTest {

	public static String generatedToken;

	/**
	 * Hits Generate Token API with provided request body
	 */
	private Response hitGenerateTokenApi(Map<String, Object> requestBody) {
		Response response = given().spec(requestSpec).body(requestBody).when()
				.post("/services/api/partner/generateToken").then().log().all().extract().response();

		listeners.TestListener.test.get().info("Request Body: " + requestBody);
		listeners.TestListener.test.get().info("Response Body: " + response.asPrettyString());

		return response;
	}

	/**
	 * Validates success response structure and values
	 */
	private void validateSuccessResponse(Response response) {
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * Validates failure response with expected error code and message
	 */
	private void validateFailureResponse(Response response, String errCode, String messageField, String messageValue) {
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), errCode);
		Assert.assertEquals(response.jsonPath().getString(messageField), messageValue);
		Assert.assertEquals(response.jsonPath().getString("token"), "");
		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * Creates default valid request body
	 */
	private Map<String, Object> getDefaultRequestBody() {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "VST-20260612153819789-3YKA5D");
		requestBody.put("crn", "1000100644");
		requestBody.put("accNo", "0050303922");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");
		requestBody.put("userIPaddress", "123.456.78");

		return requestBody;
	}

	/**
	 * TC01: Verify token generation for valid CRN and account number
	 */
	@Test(priority = 1, description = "Verify token is generated successfully for valid CRN and account number (Eligible ETB customer)")
	public void verifyGenerateTokenApiWithValidCRNAndAccountNumberForEligibleETBCustomer() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		Response response = hitGenerateTokenApi(requestBody);
		validateSuccessResponse(response);

		generatedToken = response.jsonPath().getString("token");
		Assert.assertNotNull(generatedToken);
		Assert.assertFalse(generatedToken.isEmpty());
	}

	/**
	 * TC02: Verify API response when CRN is empty
	 */
	@Test(description = "Verify API returns error when CRN is empty")
	public void verifyGenerateTokenApiWithEmptyCRN() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR1008", "errorDescription", "Technical Decline");
	}

	/**
	 * TC03: Verify API when Aadhaar is not linked
	 */
	@Test(description = "Verify API returns business decline when Aadhaar link status is 'N'")
	public void verifyGenerateTokenApiWhenAadharLinkIsN() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "27374059");
		requestBody.put("accNo", "04221040003886");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR7004", "errorDescription", "Business Decline");
	}

	/**
	 * TC04: Verify API when account number is missing
	 */
	@Test(description = "Verify API returns error when account number is missing")
	public void verifyGenerateTokenApiWithMissingMandatoryFieldAccNo() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("accNo", "");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR1008", "errorDescription", "Technical Decline");
	}

	/**
	 * TC05: Verify API when client code is missing
	 */
	@Test(description = "Verify API returns error when client code is missing")
	public void verifyGenerateTokenApiWithMissingClientCode() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("clientCode", "");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR1008", "errorDescription", "Technical Decline");
	}

	/**
	 * TC06: Verify API when CRN exceeds maximum allowed length
	 */
	@Test(description = "Verify API returns error when CRN exceeds maximum length")
	public void verifyGenerateTokenApiWithCRNAboveMaximumLength() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "1234567890123456");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR1008", "errorDescription", "Technical Decline");
	}

	/**
	 * TC07: Verify API when CRN contains alphabets
	 */
	@Test(description = "Verify API returns error when CRN contains alphabetic characters")
	public void verifyGenerateTokenApiWithAlphabeticCharactersInCRN() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "ABCD59017");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR1008", "errorDescription", "Technical Decline");
	}

	/**
	 * TC08: Verify API when account number contains special characters
	 */
	@Test(description = "Verify API returns error when account number contains special characters")
	public void verifyGenerateTokenApiWithSpecialCharactersInAccNo() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("accNo", "0958!@#$%");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR1008", "errorDescription", "Technical Decline");
	}

	/**
	 * TC09: Verify API validates customer via dependent APIs
	 */
	@Test(description = "Verify API successfully validates customer via CRN and Account Inquiry APIs")
	public void verifyGenerateTokenApiValidatesCustomerViaCRNApiAndAccountInquiryApi() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		Response response = hitGenerateTokenApi(requestBody);
		validateSuccessResponse(response);

		String token = response.jsonPath().getString("token");
		Assert.assertNotNull(token);
		Assert.assertFalse(token.isEmpty());
	}

	/**
	 * TC10: Verify API when customer is blacklisted
	 */
	@Test(description = "Verify API returns business decline when customer CRN is blacklisted")
	public void verifyGenerateTokenApiWhenCustomerCRNStatusIsBlacklisted() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "8593560");
		requestBody.put("accNo", "08160140009332");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR7002", "errorDescription", "Business Decline");
	}

	/**
	 * TC11: Verify API with non-existent CRN
	 */
	@Test(description = "Verify API returns business decline for non-existent CRN")
	public void verifyGenerateTokenApiWithInvalidNonExistentCRN() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "99999999");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR7002", "errorDescription", "Business Decline");
	}

	/**
	 * TC12: Verify API when party type is corporate
	 */
	@Test(description = "Verify API returns business decline when party type is corporate")
	public void verifyGenerateTokenApiWhenPartyTypeIsCorporate() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "3397636");
		requestBody.put("accNo", "6311175802");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR7003", "errorDescription", "Business Decline");
	}

	/**
	 * TC13: Verify API when IT type is not allowed
	 */
	@Test(description = "Verify API returns business decline when IT type is not allowed")
	public void verifyGenerateTokenApiWhenITTypeIsNotAllowed() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "1005817");
		requestBody.put("accNo", "8111200890");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR7001", "errorDescription", "Business Decline");
	}

	/**
	 * TC14: Verify API when account status is dormant
	 */
	@Test(description = "Verify API returns business decline when account status is Dormant")
	public void verifyGenerateTokenApiWhenAccountStatusIsDormant() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "1000816994");
		requestBody.put("accNo", "0050910397");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR7010", "errorDescription", "Business Decline");
	}

	/**
	 * TC15: Verify API when account status is debit freeze
	 */
	@Test(description = "Verify API returns business decline when account status is Debit Freeze")
	public void verifyGenerateTokenApiWhenAccountStatusIsDebitFreeze() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "1000851875");
		requestBody.put("accNo", "0050910427");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR7010", "errorDescription", "Business Decline");
	}

	/**
	 * TC16: Verify API when relation type is not allowed
	 */
	@Test(description = "Verify API returns business decline when relation type is not allowed")
	public void verifyGenerateTokenApiWhenRelationTypeIsNotAllowed() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "7706040");
		requestBody.put("accNo", "09581040001571");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR7008", "errorDescription", "Business Decline");
	}

	/**
	 * TC17: Verify API with CRN at maximum allowed length
	 */
	@Test(description = "Verify API works successfully when CRN is at maximum allowed length")
	public void verifyGenerateTokenApiWithCrnAtMaximumLength() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "100000081000");
		requestBody.put("accNo", "0050262120");

		Response response = hitGenerateTokenApi(requestBody);
		validateSuccessResponse(response);
	}

	/**
	 * TC18: Verify API with CRN at minimum length (4 digits)
	 */
	@Test(description = "Verify API returns failure when CRN is exactly 4 digits (minimum length boundary case)")
	public void verifyGenerateTokenApiWithCrnAtMinimumLengthFourDigits() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "1234");
		requestBody.put("accNo", "0050303922");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR1008", "errorDescription", "Technical Decline");
	}

	/**
	 * TC19: Verify API with CRN above maximum allowed length (16 digits)
	 */
	@Test(description = "Verify API returns failure when CRN exceeds maximum allowed length (16 digits)")
	public void verifyGenerateTokenApiWithCrnAboveMaximumLength() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "1234567890123456");
		requestBody.put("accNo", "0050303922");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR1008", "errorDescription", "Technical Decline");
	}

	/**
	 * TC20: Verify API when customer CRN status is Suspended
	 */
	@Test(description = "Verify API returns business decline when customer CRN status is Suspended")
	public void verifyGenerateTokenApiWhenCustomerCRNStatusIsSuspended() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "24213234");
		requestBody.put("accNo", "06381040000773");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR7002", "errorDescription", "Business Decline");
	}

	/**
	 * TC21: Verify API when account number does not match any APAC in BCIF response
	 */
	@Test(description = "Verify API returns business decline when account number does not match any APAC in BCIF response")
	public void verifyGenerateTokenApiWhenAccNoDoesNotMatchApacInBCIF() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "1000100644");
		requestBody.put("accNo", "0050303924");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR7007", "errorDescription", "Business Decline");
	}

	/**
	 * TC22: Verify API when account status is Credit Freeze (C)
	 */
	@Test(description = "Verify API returns business decline when account status is Credit Freeze (C)")
	public void verifyGenerateTokenApiWhenAccountStatusIsCreditFreeze() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "1000851901");
		requestBody.put("accNo", "0050910410");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR7010", "errorDescription", "Business Decline");
	}

	/**
	 * TC23: Verify API when account status is Total Freeze (T)
	 */
	@Test(description = "Verify API returns business decline when account status is Total Freeze (T)")
	public void verifyGenerateTokenApiWhenAccountStatusIsTotalFreeze() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "9999995211");
		requestBody.put("accNo", "9949856068");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR7010", "errorDescription", "Business Decline");
	}

	/**
	 * TC24: Verify API when SchemeCode is not in allowed list
	 */
	@Test(description = "Verify API returns business decline when SchemeCode is not in allowed list")
	public void verifyGenerateTokenApiWhenSchemeCodeIsNotAllowed() {
		Map<String, Object> requestBody = getDefaultRequestBody();
		requestBody.put("crn", "100759017");
		requestBody.put("accNo", "7150157172");

		Response response = hitGenerateTokenApi(requestBody);
		validateFailureResponse(response, "ERR7009", "errorDescription", "Business Decline");
	}
}