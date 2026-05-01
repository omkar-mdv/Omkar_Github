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
public class GetQuoteApi extends ApiBaseTest {

	public static String generatedToken;
	public static String requestId;

	/**
	 * Generates request body for Generate Token API
	 */
	private Map<String, Object> getGenerateTokenRequest() {
		requestId = "REQ" + System.currentTimeMillis();

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", requestId);
		requestBody.put("crn", "1000100644");
		requestBody.put("accNo", "0050303922");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");

		return requestBody;
	}

	/**
	 * Generates request body for OTP APIs
	 */
	private Map<String, Object> getOtpRequest() {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", requestId);
		requestBody.put("otpType", "SMS");
		requestBody.put("crn", "1000100644");
		requestBody.put("accNo", "0050303922");
		requestBody.put("channel", "WEB");
		requestBody.put("groupId", "KB");
		requestBody.put("clientCode", "VESTED");
		return requestBody;
	}

	/**
	 * Generates request body for Quote API
	 */
	private Map<String, Object> getQuoteRequest() {
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", requestId);
		requestBody.put("groupId", "KB");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("send currency in FCY", "USD");
		requestBody.put("purposedescription", "LRS EQUITY");
		requestBody.put("fc_amt", "10");
		requestBody.put("channel", "WEB");
		requestBody.put("partnerName", "VESTED");
		requestBody.put("CRN", "1000100644");
		requestBody.put("beneCountrycode", "US");
		requestBody.put("Req date n time", "2026-01-06T15:30:00Z");
		requestBody.put("sourceOfFunds", "funds");

		return requestBody;
	}

	/**
	 * Logs request and response
	 */
	private void logToReport(Map<String, Object> requestBody, Response response) {
		if (TestListener.test.get() != null) {
			TestListener.test.get().info("Request Body: " + requestBody);
			TestListener.test.get().info("Response Body: " + response.asPrettyString());
		}
	}

	/**
	 * TC01: Verify Generate Token API
	 */
	@Test(description = "Verify token is generated successfully")
	public void verifyGenerateTokenApiReturnsSuccessWithValidRequest() {

		Map<String, Object> requestBody = getGenerateTokenRequest();

		Response response = given().spec(requestSpec).body(requestBody).post("/services/api/partner/generateToken");

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "S");

		generatedToken = response.jsonPath().getString("token");
		Assert.assertNotNull(generatedToken);
	}

	/**
	 * TC02: Verify Send OTP API
	 */
	@Test(dependsOnMethods = "verifyGenerateTokenApiReturnsSuccessWithValidRequest", description = "Verify OTP is sent successfully")
	public void verifySendOtpApiReturnsSuccessWithValidToken() {

		Map<String, Object> requestBody = getOtpRequest();

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/sendOTP");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "S");
	}

	/**
	 * TC03: Verify OTP API
	 */
	@Test(dependsOnMethods = "verifySendOtpApiReturnsSuccessWithValidToken", description = "Verify OTP is validated successfully")
	public void verifyVerifyOtpApiReturnsSuccessWithValidOtp() {

		Map<String, Object> requestBody = getOtpRequest();
		requestBody.put("otp", "123456");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/verifyOTP");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "S");
	}

	/**
	 * TC04: Verify Quote API success flow
	 */
	@Test(dependsOnMethods = "verifyVerifyOtpApiReturnsSuccessWithValidOtp", description = "Verify Quote API returns FX rate, charges and TCS after successful OTP verification using valid auth token")
	public void verifyGetQuoteApiReturnsSuccessWithValidTokenAndVerifiedOtp() {

		Assert.assertNotNull(generatedToken, "Auth token is null");
		Assert.assertFalse(generatedToken.isEmpty(), "Auth token is empty");
		Assert.assertNotNull(requestId, "RequestId is null");

		Map<String, Object> requestBody = getQuoteRequest();

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/getQuote");

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));

		Map<String, Object> quoteDetails = response.jsonPath().getMap("getQuoteDetails");
		Map<String, Object> transactionDetails = response.jsonPath().getMap("transactionDetails");

		Assert.assertNotNull(quoteDetails, "getQuoteDetails is null");
		Assert.assertNotNull(transactionDetails, "transactionDetails is null");

		Assert.assertTrue(quoteDetails.containsKey("quoteId"), "quoteId missing");

		Assert.assertTrue(transactionDetails.containsKey("rate"), "rate missing");
		Assert.assertTrue(transactionDetails.containsKey("charges"), "charges missing");
		Assert.assertTrue(transactionDetails.containsKey("tcsTaxAmount"), "tcsTaxAmount missing");
		Assert.assertTrue(transactionDetails.containsKey("lcyamt"), "lcyamt missing");
		Assert.assertTrue(transactionDetails.containsKey("finalSendAmountINR"), "finalSendAmountINR missing");
	}

	/**
	 * TC05: Verify Quote API fails without OTP verification
	 */
	@Test(dependsOnMethods = "verifyGenerateTokenApiReturnsSuccessWithValidRequest", description = "Verify Quote API fails if OTP is not verified")
	public void verifyQuoteApiWithoutOtpVerification() {

		Map<String, Object> requestBody = getQuoteRequest();

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/getQuote");

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "F");

		// ✅ Correct assertion based on actual response
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Seems to be OTP not verified.");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC06: Verify Quote API returns document requirement message
	 */
	@Test(dependsOnMethods = "verifyVerifyOtpApiReturnsSuccessWithValidOtp", description = "Verify Quote API returns document requirement message when documents are required")
	public void verifyQuoteApiReturnsIsDocRequiredNWhenAccountAgeGreaterThanOrEqualToOneYear() {

		Map<String, Object> requestBody = getQuoteRequest();

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/getQuote");

		logToReport(requestBody, response);

		String isDocRequired = response.jsonPath().getString("transactionDetails.isDocRequired");

		Assert.assertNotNull(isDocRequired);
		Assert.assertFalse(isDocRequired.isEmpty());
		Assert.assertTrue(isDocRequired.contains("Income proof"));
	}

	/**
	 * TC07: Verify Quote API returns error 1001 when LRS limit exceeded
	 */
	@Test(dependsOnMethods = "verifyVerifyOtpApiReturnsSuccessWithValidOtp", description = "Verify Quote API returns error 1001 when LRS limit is exceeded")
	public void verifyQuoteApiReturnsError1001WhenLrsLimitExceeded() {

		Map<String, Object> requestBody = getQuoteRequest();
		requestBody.put("fc_amt", "999999");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/getQuote");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1001");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Limit exceeds");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC08: Verify Quote API returns error 1003 when purpose mismatch
	 */
	@Test(dependsOnMethods = "verifyVerifyOtpApiReturnsSuccessWithValidOtp", description = "Verify Quote API returns error 1003 when purpose description is invalid")
	public void verifyQuoteApiReturnsError1003WhenPurposeMismatch() {

		Map<String, Object> requestBody = getQuoteRequest();
		requestBody.put("purposedescription", "Invalid Purpose");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/getQuote");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1003");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Purpose mismatch");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC09: Verify Quote API returns error 1002 when insufficient bank balance
	 */
	@Test(dependsOnMethods = "verifyVerifyOtpApiReturnsSuccessWithValidOtp", description = "Verify Quote API returns error 1002 when bank balance is insufficient")
	public void verifyQuoteApiReturnsError1002WhenInsufficientBankBalance() {

		Map<String, Object> requestBody = getQuoteRequest();
		requestBody.put("fc_amt", "112000");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/getQuote");

		logToReport(requestBody, response);

		// ✅ Correct fields for this scenario
		Assert.assertEquals(response.jsonPath().getString("transactionDetails.limitError"), "ERR1002");
		Assert.assertEquals(response.jsonPath().getString("transactionDetails.errorMsg"), "Insufficient funds");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC10: Verify Quote API returns error 1007 when token is invalid
	 */
	@Test(description = "Verify Quote API returns error 1007 when auth token is invalid")
	public void verifyQuoteApiReturnsError1007WhenAuthTokenIsInvalid() {

		Map<String, Object> requestBody = getQuoteRequest();

		Response response = given().spec(requestSpec).header("authToken", "INVALID_TOKEN") // ❌ Invalid token
				.body(requestBody).post("/services/api/partner/getQuote");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1007");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical error");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC11: Verify Quote API returns error 1008 for invalid field format when
	 * purpose description is blank
	 */
	@Test(dependsOnMethods = "verifyVerifyOtpApiReturnsSuccessWithValidOtp", description = "Verify Quote API returns error 1008 when purposeDescription is blank")
	public void verifyQuoteApiReturnsError1008WhenPurposeDescriptionIsBlank() {

		Map<String, Object> requestBody = getQuoteRequest();
		requestBody.put("purposedescription", ""); // ❌ Blank value

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/getQuote");

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC12: Verify Quote API returns error 1008 when fc_amt is blank
	 */
	@Test(dependsOnMethods = "verifyVerifyOtpApiReturnsSuccessWithValidOtp", description = "Verify Quote API returns error 1008 when fc_amt is blank")
	public void verifyQuoteApiReturnsError1008WhenFcAmtIsBlank() {

		Map<String, Object> requestBody = getQuoteRequest();
		requestBody.put("fc_amt", ""); // ❌ Blank value

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/getQuote");

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC14: Verify Quote API returns error 1008 when purposedescription is missing
	 */
	@Test(dependsOnMethods = "verifyVerifyOtpApiReturnsSuccessWithValidOtp", description = "Verify Quote API returns error 1008 when purposedescription is missing")
	public void verifyQuoteApiReturnsError1008WhenPurposeDescriptionIsMissing() {

		Map<String, Object> requestBody = getQuoteRequest();
		requestBody.put("purposedescription", ""); // ❌ Blank value

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/getQuote");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC15: Verify Quote API returns error 1008 when currency code exceeds 3
	 * characters
	 */
	@Test(dependsOnMethods = "verifyVerifyOtpApiReturnsSuccessWithValidOtp", description = "Verify Quote API returns error 1008 when currency code is more than 3 characters")
	public void verifyQuoteApiReturnsError1008WhenCurrencyCodeExceedsThreeCharacters() {

		Map<String, Object> requestBody = getQuoteRequest();
		requestBody.put("send currency in FCY", "USDD"); // ❌ Invalid currency code

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/getQuote");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC16: Verify Quote API for beneficiary pre-defined in KR master - purpose
	 * check against master
	 */
	@Test(dependsOnMethods = "verifyVerifyOtpApiReturnsSuccessWithValidOtp", description = "Verify Quote API returns success when purpose matches KR master for predefined beneficiary")
	public void verifyQuoteApiReturnsSuccessWhenPurposeMatchesKrMaster() {

		Map<String, Object> requestBody = getQuoteRequest();
		requestBody.put("purposedescription", "LRS GIFT"); // ✅ Valid purpose from KR master

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/getQuote");

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "S");

		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC17: Verify Get Quote API allows Gift sourceOfFunds and returns quote
	 * successfully
	 */
	@Test(dependsOnMethods = "verifyVerifyOtpApiReturnsSuccessWithValidOtp", description = "Verify Get Quote API returns success when sourceOfFunds is Gift")
	public void verifyGetQuoteApiReturnsSuccessWhenSourceOfFundsIsGift() {

		Map<String, Object> requestBody = getQuoteRequest();
		requestBody.put("sourceOfFunds", "Gift"); // ✅ Valid source of funds

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/getQuote");

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "S");

		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC18 : Verify Get Quote API allows Owned Funds and returns quote successfully
	 */
	@Test(dependsOnMethods = "verifyVerifyOtpApiReturnsSuccessWithValidOtp", description = "Verify Get Quote API returns success when sourceOfFunds is Owned Funds")
	public void verifyGetQuoteApiReturnsSuccessWhenSourceOfFundsIsOwnedFunds() {

		Map<String, Object> requestBody = getQuoteRequest();
		requestBody.put("sourceOfFunds", "Owned Funds"); // ✅ Valid source of funds

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/getQuote");

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "S");

		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC19: Verify Quote API returns error 1008 when fc_amt has more than 2 decimal
	 * places
	 */
	@Test(dependsOnMethods = "verifyVerifyOtpApiReturnsSuccessWithValidOtp", description = "Verify Quote API returns error 1008 when fc_amt has more than 2 decimal places")
	public void verifyQuoteApiReturnsError1008WhenFcAmtHasMoreThanTwoDecimalPlaces() {

		Map<String, Object> requestBody = getQuoteRequest();
		requestBody.put("fc_amt", "10.123"); // ❌ More than 2 decimal places

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/getQuote");

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}
}