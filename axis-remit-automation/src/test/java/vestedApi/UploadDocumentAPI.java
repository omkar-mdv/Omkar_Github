package vestedApi;

import io.restassured.response.Response;
import listeners.TestListener;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import base.ApiBaseTest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Listeners(TestListener.class)
public class UploadDocumentAPI extends ApiBaseTest {

	public static String generatedToken;
	public static String requestId;

	public static String quoteId;
	public static String checkSum;
	public static String finalSendAmountINR;
	public static String conversionRate;
	public static String recvAmount;
	public static String txnRefNo;

	private static final String BASE64_BELOW_250KB = "src/test/resources/testdata/base64Below250Kb.txt";

	private static final String BASE64_ABOVE_250KB = "src/test/resources/testdata/base64Above250Kb.txt";

	private static final String BASE64_UNSUPPORTED = "src/test/resources/testdata/base64Unsupported.txt";

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
	private Map<String, Object> getQuoteRequest(String sourceOfFunds) {
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
		requestBody.put("sourceOfFunds", sourceOfFunds); // ✅ dynamic
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
	 * Generates request body for Upload Document API
	 */
	private Map<String, Object> getUploadDocumentRequest(String filePath) {

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", requestId);
		requestBody.put("docType", "Passport");
		requestBody.put("groupId", "KB");
		requestBody.put("comment", "Remit Document");
		requestBody.put("txnRefNo", txnRefNo);
		requestBody.put("crn", "1000100644");
		requestBody.put("fileName", "Test.pdf");
		requestBody.put("uniqueId", "UID001");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("documentCount", "1");
		requestBody.put("accNo", "0050303922");
		requestBody.put("documentId", "124");
		requestBody.put("customerConsent", "N");

		requestBody.put("content", getBase64Content(filePath));

		return requestBody;
	}

	private void generateNewTransaction(String sourceOfFunds) {

		// Step 1: Get Quote
		Response quoteResponse = given().spec(requestSpec).header("authToken", generatedToken)
				.body(getQuoteRequest(sourceOfFunds)).post("/services/api/partner/getQuote");

		Assert.assertEquals(quoteResponse.jsonPath().getString("status"), "S");

		quoteId = quoteResponse.jsonPath().getString("getQuoteDetails.quoteId");
		recvAmount = quoteResponse.jsonPath().getString("getQuoteDetails.fc_amt");
		conversionRate = quoteResponse.jsonPath().getString("transactionDetails.rate");
		finalSendAmountINR = quoteResponse.jsonPath().getString("transactionDetails.lcyamt");
		checkSum = quoteResponse.jsonPath().getString("transactionDetails.checkSum");

		// Step 2: Book Transaction
		Response bookResponse = given().spec(requestSpec).header("authToken", generatedToken)
				.body(getBookTransactionRequest()).post("/services/api/partner/bookTransaction");

		Assert.assertEquals(bookResponse.jsonPath().getString("status"), "S");

		txnRefNo = bookResponse.jsonPath().getString("txnRefNo");
		Assert.assertNotNull(txnRefNo);
	}

	/**
	 * Read Base64 content from file
	 */
	private String getBase64Content(String filePath) {
		try {
			return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));
		} catch (Exception e) {
			throw new RuntimeException("Unable to read base64 file", e);
		}
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
	@Test
	public void verifyGenerateTokenApiReturnsSuccessWithValidRequest() {
		Response response = given().spec(requestSpec).body(getGenerateTokenRequest())
				.post("/services/api/partner/generateToken");

		generatedToken = response.jsonPath().getString("token");

		Assert.assertEquals(response.jsonPath().getString("status"), "S");
	}

	/**
	 * TC02: Verify Send OTP API
	 */
	@Test(dependsOnMethods = "verifyGenerateTokenApiReturnsSuccessWithValidRequest")
	public void verifySendOtpApiReturnsSuccessWithValidToken() {
		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(getOtpRequest())
				.post("/services/api/partner/sendOTP");

		Assert.assertEquals(response.jsonPath().getString("status"), "S");
	}

	/**
	 * TC03: Verify OTP API
	 */
	@Test(dependsOnMethods = "verifySendOtpApiReturnsSuccessWithValidToken")
	public void verifyVerifyOtpApiReturnsSuccessWithValidOtp() {

		Map<String, Object> requestBody = getOtpRequest();
		requestBody.put("otp", "123456");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/verifyOTP");

		Assert.assertEquals(response.jsonPath().getString("status"), "S");
	}

	/**
	 * TC04: Verify Quote API success flow
	 */
	@Test(dependsOnMethods = "verifyVerifyOtpApiReturnsSuccessWithValidOtp")
	public void verifyGetQuoteApiReturnsSuccessWithValidTokenAndVerifiedOtp() {

		Response response = given().spec(requestSpec).header("authToken", generatedToken)
				.body(getQuoteRequest("Owned Funds")).post("/services/api/partner/getQuote");

		Assert.assertEquals(response.jsonPath().getString("status"), "S");

		quoteId = response.jsonPath().getString("getQuoteDetails.quoteId");
		recvAmount = response.jsonPath().getString("getQuoteDetails.fc_amt");
		conversionRate = response.jsonPath().getString("transactionDetails.rate");
		finalSendAmountINR = response.jsonPath().getString("transactionDetails.lcyamt");
		checkSum = response.jsonPath().getString("transactionDetails.checkSum");
	}

	/**
	 * TC05: Verify Book Transaction API success flow
	 */
	@Test(dependsOnMethods = "verifyGetQuoteApiReturnsSuccessWithValidTokenAndVerifiedOtp")
	public void verifyBookTransactionApiReturnsSuccessWithValidData() {

		Response response = given().spec(requestSpec).header("authToken", generatedToken)
				.body(getBookTransactionRequest()).post("/services/api/partner/bookTransaction");

		Assert.assertEquals(response.jsonPath().getString("status"), "S");

		txnRefNo = response.jsonPath().getString("txnRefNo");
		Assert.assertNotNull(txnRefNo);
	}

	/**
	 * TC06: Verify Upload Document API returns success when file size is within
	 * 250KB
	 */
	@Test(dependsOnMethods = "verifyBookTransactionApiReturnsSuccessWithValidData", description = "Verify Upload Document API returns success when file size is within 250KB")
	public void verifyUploadDocumentApiReturnsSuccessWhenFileSizeIsWithinLimit() {

		Assert.assertNotNull(generatedToken);
		Assert.assertNotNull(txnRefNo);

		Map<String, Object> requestBody = getUploadDocumentRequest(BASE64_BELOW_250KB);

		// ✅ Below 250KB file
		requestBody.put("content", getBase64Content("src/test/resources/testdata/base64Below250Kb.txt"));

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/uploadDocument");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "S");

		Assert.assertEquals(response.jsonPath().getString("documentResults[0].uploadStatus"), "S");

		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC07: Verify Upload Document API returns error 1008 when file size exceeds
	 * 250KB
	 */
	@Test(dependsOnMethods = "verifyBookTransactionApiReturnsSuccessWithValidData", description = "Verify Upload Document API returns error when file exceeds 250KB")
	public void verifyUploadDocumentApiReturnsError1008WhenFileSizeExceedsLimit() {

		Assert.assertNotNull(generatedToken);
		Assert.assertNotNull(txnRefNo);

		Map<String, Object> requestBody = getUploadDocumentRequest("src/test/resources/testdata/base64Above250Kb.txt");

		// ❌ Above 250KB file
		requestBody.put("content", getBase64Content(BASE64_ABOVE_250KB));

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/uploadDocument");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC08: Verify Upload Document API returns error when unsupported file type is
	 * uploaded
	 */
	@Test(dependsOnMethods = "verifyBookTransactionApiReturnsSuccessWithValidData", description = "Verify Upload Document API returns error for unsupported file type")
	public void verifyUploadDocumentApiReturnsErrorForUnsupportedFile() {

		Map<String, Object> requestBody = getUploadDocumentRequest(BASE64_UNSUPPORTED);

		// ❌ You may also want to set mismatched docType
		requestBody.put("docType", "EXE"); // or keep PDF depending on API validation

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/uploadDocument");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC09: Verify Upload Document API returns error 1008 when uniqueId is blank
	 */
	@Test(dependsOnMethods = "verifyBookTransactionApiReturnsSuccessWithValidData", description = "Verify Upload Document API returns error 1008 when uniqueId is blank")
	public void verifyUploadDocumentApiReturnsError1008WhenUniqueIdIsBlank() {

		Assert.assertNotNull(generatedToken);
		Assert.assertNotNull(txnRefNo);

		Map<String, Object> requestBody = getUploadDocumentRequest(BASE64_BELOW_250KB);

		// ❌ Blank uniqueId
		requestBody.put("uniqueId", "");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/uploadDocument");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC10: Verify Upload Document API returns error 1008 when content field is
	 * blank
	 */
	@Test(dependsOnMethods = "verifyBookTransactionApiReturnsSuccessWithValidData", description = "Verify Upload Document API returns error 1008 when content is blank")
	public void verifyUploadDocumentApiReturnsError1008WhenContentIsBlank() {

		Assert.assertNotNull(generatedToken);
		Assert.assertNotNull(txnRefNo);

		Map<String, Object> requestBody = getUploadDocumentRequest(BASE64_BELOW_250KB);

		// ❌ Blank content
		requestBody.put("content", "");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/uploadDocument");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC11: Verify Upload Document API returns error 1007 when auth token is
	 * invalid
	 */
	@Test(description = "Verify Upload Document API returns error 1007 when auth token is invalid")
	public void verifyUploadDocumentApiReturnsError1007WhenAuthTokenIsInvalid() {

		Map<String, Object> requestBody = getUploadDocumentRequest(BASE64_BELOW_250KB);

		Response response = given().spec(requestSpec).header("authToken", "INVALID_TOKEN") // ❌ Invalid token
				.body(requestBody).post("/services/api/partner/uploadDocument");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1007");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Token issue");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC12: Verify Upload Document API allows Gift with customerConsent = Y
	 */
	@Test(dependsOnMethods = "verifyBookTransactionApiReturnsSuccessWithValidData")
	public void verifyUploadDocumentApiReturnsSuccessWhenSourceOfFundsIsGiftAndCustomerConsentIsY() {

		generateNewTransaction("Gift"); // ✅ NEW txn

		Map<String, Object> requestBody = getUploadDocumentRequest(BASE64_BELOW_250KB);
		requestBody.put("customerConsent", "Y");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/uploadDocument");

		Assert.assertEquals(response.jsonPath().getString("status"), "S");
	}

	/**
	 * TC13: Verify Upload Document API declines Gift when customerConsent = N
	 */
	@Test(dependsOnMethods = "verifyBookTransactionApiReturnsSuccessWithValidData")
	public void verifyUploadDocumentApiReturnsBusinessDeclineWhenSourceOfFundsIsGiftAndCustomerConsentIsN() {

		generateNewTransaction("Gift"); // ✅ NEW txn

		Map<String, Object> requestBody = getUploadDocumentRequest(BASE64_BELOW_250KB);
		requestBody.put("customerConsent", "N");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/uploadDocument");

		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Business Decline");
	}

	/**
	 * TC14: Verify Upload Document API allows Owned Funds when customerConsent = Y
	 */
	@Test(dependsOnMethods = "verifyBookTransactionApiReturnsSuccessWithValidData")
	public void verifyUploadDocumentApiReturnsSuccessWhenSourceOfFundsIsOwnedFundsAndCustomerConsentIsY() {

		generateNewTransaction("Owned Funds"); // ✅ NEW txn

		Map<String, Object> requestBody = getUploadDocumentRequest(BASE64_BELOW_250KB);
		requestBody.put("customerConsent", "Y");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/uploadDocument");

		Assert.assertEquals(response.jsonPath().getString("status"), "S");
	}

	/**
	 * TC15: Verify Upload Document API allows Owned Funds when customerConsent = N
	 */
	@Test(dependsOnMethods = "verifyBookTransactionApiReturnsSuccessWithValidData")
	public void verifyUploadDocumentApiReturnsSuccessWhenSourceOfFundsIsOwnedFundsAndCustomerConsentIsN() {

		generateNewTransaction("Owned Funds"); // ✅ NEW txn

		Map<String, Object> requestBody = getUploadDocumentRequest(BASE64_BELOW_250KB);
		requestBody.put("customerConsent", "N");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/uploadDocument");

		Assert.assertEquals(response.jsonPath().getString("status"), "S");
	}

	/**
	 * TC16: Verify Upload Document API returns error 1008 when customerConsent is
	 * blank for Gift
	 */
	@Test(dependsOnMethods = "verifyBookTransactionApiReturnsSuccessWithValidData", description = "Verify Upload Document API returns error 1008 when sourceOfFunds is Gift and customerConsent is blank")
	public void verifyUploadDocumentApiReturnsError1008WhenCustomerConsentIsBlankForGift() {

		Assert.assertNotNull(generatedToken);
		Assert.assertNotNull(txnRefNo);

		Map<String, Object> requestBody = getUploadDocumentRequest(BASE64_BELOW_250KB);

		// ❌ Blank consent (mandatory for Gift)
		requestBody.put("customerConsent", "");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/uploadDocument");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC17: Verify Upload Document API returns error 1008 when customerConsent has
	 * invalid value
	 */
	@Test(dependsOnMethods = "verifyBookTransactionApiReturnsSuccessWithValidData", description = "Verify Upload Document API returns error 1008 when customerConsent is invalid (e.g., X)")
	public void verifyUploadDocumentApiReturnsError1008WhenCustomerConsentIsInvalid() {

		Assert.assertNotNull(generatedToken);
		Assert.assertNotNull(txnRefNo);

		Map<String, Object> requestBody = getUploadDocumentRequest(BASE64_BELOW_250KB);

		// ❌ Invalid value
		requestBody.put("customerConsent", "X");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/uploadDocument");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}
}