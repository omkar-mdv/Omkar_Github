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
public class BookTransactionAPI extends ApiBaseTest {

	public static String generatedToken;
	public static String requestId;

	public static String quoteId;
	public static String checkSum;
	public static String finalSendAmountINR;
	public static String conversionRate;
	public static String recvAmount;

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
		requestBody.put("userIPaddress", "123.456.78");

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
		requestBody.put("sourceOfFunds", "Owned Funds");
		return requestBody;
	}

	/**
	 * Generates request body for Book Transaction API
	 */
	private Map<String, Object> getBookTransactionRequest() {

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", requestId);
		requestBody.put("groupId", "KB");
		requestBody.put("TnCdeclarationFlag", "Y");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("CRN", "1000100644");

		// ✅ Mapping from Quote API
		requestBody.put("quoteId", quoteId);
		requestBody.put("checkSum", checkSum);
		requestBody.put("recvAmount", recvAmount);
		requestBody.put("conversionRate", conversionRate);
		requestBody.put("finalSendAmountINR", finalSendAmountINR);

		requestBody.put("channel", "WEB");
		requestBody.put("partnerName", "VESTED");
		requestBody.put("requestDateTime", "2026-01-19T11:45:30");

		Map<String, Object> beneficiary = new HashMap<>();
		beneficiary.put("BeneAndBeneBankDetailsToFrom", "Bank");

		requestBody.put("beneficiary", beneficiary);

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
	 * TC04: Verify Quote API and capture required values
	 */
	@Test(dependsOnMethods = "verifyVerifyOtpApiReturnsSuccessWithValidOtp", description = "Verify Quote API returns success and capture values for Book Transaction")
	public void verifyGetQuoteApiReturnsSuccessWithValidTokenAndVerifiedOtp() {

		Map<String, Object> requestBody = getQuoteRequest();

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/getQuote");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "S");

		// ✅ Correct capture
		quoteId = response.jsonPath().getString("getQuoteDetails.quoteId");
		recvAmount = response.jsonPath().getString("getQuoteDetails.fc_amt");
		conversionRate = response.jsonPath().getString("transactionDetails.rate");
		finalSendAmountINR = response.jsonPath().getString("transactionDetails.finalSendAmountINR");
		checkSum = response.jsonPath().getString("transactionDetails.checkSum");
	}

	/**
	 * TC05: Verify Book Transaction API
	 */
	@Test(dependsOnMethods = "verifyGetQuoteApiReturnsSuccessWithValidTokenAndVerifiedOtp", description = "Verify Book Transaction API returns success using quote response data")
	public void verifyBookTransactionApiReturnsSuccessWithValidQuoteData() {

		Assert.assertNotNull(generatedToken);
		Assert.assertNotNull(quoteId);

		Map<String, Object> requestBody = getBookTransactionRequest();

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/bookTransaction");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "S");

		String txnRefNo = response.jsonPath().getString("txnRefNo");
		Assert.assertNotNull(txnRefNo);

		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC06: Verify Book Transaction API returns error 1008 when declarationFlag is
	 * blank
	 */
	@Test(dependsOnMethods = "verifyGetQuoteApiReturnsSuccessWithValidTokenAndVerifiedOtp", description = "Verify Book Transaction API returns error 1008 when declarationFlag is blank")
	public void verifyBookTransactionApiReturnsError1008WhenDeclarationFlagIsBlank() {

		Assert.assertNotNull(generatedToken);
		Assert.assertNotNull(quoteId);
		Assert.assertNotNull(checkSum);

		Map<String, Object> requestBody = getBookTransactionRequest();

		// ❌ Blank declaration flag
		requestBody.put("TnCdeclarationFlag", "");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/bookTransaction");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC07: Verify Book Transaction API returns error 7001 when declarationFlag is
	 * N
	 */
	@Test(dependsOnMethods = "verifyGetQuoteApiReturnsSuccessWithValidTokenAndVerifiedOtp", description = "Verify Book Transaction API returns error 7001 when declarationFlag is N")
	public void verifyBookTransactionApiReturnsError7001WhenDeclarationFlagIsN() {

		Assert.assertNotNull(generatedToken);
		Assert.assertNotNull(quoteId);
		Assert.assertNotNull(checkSum);

		Map<String, Object> requestBody = getBookTransactionRequest();

		// ❌ Set declaration flag to N
		requestBody.put("TnCdeclarationFlag", "N");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/bookTransaction");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR7001");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Business Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC08: Verify Book Transaction API returns error 1008 when checkSum is invalid
	 */
	@Test(dependsOnMethods = "verifyGetQuoteApiReturnsSuccessWithValidTokenAndVerifiedOtp", description = "Verify Book Transaction API returns error 1008 when checkSum is invalid")
	public void verifyBookTransactionApiReturnsError1008WhenCheckSumIsInvalid() {

		Assert.assertNotNull(generatedToken);
		Assert.assertNotNull(quoteId);
		Assert.assertNotNull(checkSum);

		Map<String, Object> requestBody = getBookTransactionRequest();

		// ❌ Tamper checksum
		requestBody.put("checkSum", "X9K3P7L2Q1");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/bookTransaction");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC09: Verify Book Transaction API returns error 1008 when CLIENT beneficiary
	 * is selected but details are missing
	 */
	@Test(dependsOnMethods = "verifyGetQuoteApiReturnsSuccessWithValidTokenAndVerifiedOtp", description = "Verify Book Transaction API returns error 1008 when CLIENT beneficiary is selected but beneficiary details are missing")
	public void verifyBookTransactionApiReturnsError1008WhenClientBeneDetailsAreMissing() {

		Assert.assertNotNull(generatedToken);
		Assert.assertNotNull(quoteId);
		Assert.assertNotNull(checkSum);

		Map<String, Object> requestBody = getBookTransactionRequest();

		// ❌ Set beneficiary type to CLIENT but do not provide required details
		Map<String, Object> beneficiary = new HashMap<>();
		beneficiary.put("BeneAndBeneBankDetailsToFrom", "CLIENT");

		requestBody.put("beneficiary", beneficiary);

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/bookTransaction");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC10: Verify Book Transaction API returns error 1007 when auth token is
	 * invalid
	 */
	@Test(description = "Verify Book Transaction API returns error 1007 when auth token is invalid")
	public void verifyBookTransactionApiReturnsError1007WhenAuthTokenIsInvalid() {

		Map<String, Object> requestBody = getBookTransactionRequest();

		Response response = given().spec(requestSpec).header("authToken", "INVALID_TOKEN") // ❌ Invalid token
				.body(requestBody).post("/services/api/partner/bookTransaction");

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1007");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Token issue");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}
}