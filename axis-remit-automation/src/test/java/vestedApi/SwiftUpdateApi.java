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
public class SwiftUpdateApi extends ApiBaseTest {

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
	 * Generates request body for Swift Update API
	 */
	private Map<String, Object> getSwiftTxnRequest() {

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("groupId", "KB");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("txnRefNo", "KB10003330970010");
		requestBody.put("requestId", requestId);

		return requestBody;
	}

	/**
	 * Logs request and response to report
	 */
	private void logToReport(Map<String, Object> requestBody, Response response) {

		if (TestListener.test.get() != null) {
			TestListener.test.get().info("Request Body: " + requestBody);
			TestListener.test.get().info("Response Body: " + response.asPrettyString());
		}
	}

	/**
	 * TC01: Verify Generate Token API with valid inputs
	 */
	@Test(description = "Verify token is generated successfully for valid CRN and account number")
	public void verifyGenerateTokenApiWithValidCRNAndAccountNumber() {

		Map<String, Object> requestBody = getGenerateTokenRequest();

		Response response = given().spec(requestSpec).body(requestBody).post("/services/api/partner/generateToken");

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));

		generatedToken = response.jsonPath().getString("token");

		Assert.assertNotNull(generatedToken);
		Assert.assertFalse(generatedToken.isEmpty());
	}

	/**
	 * TC02: Verify Swift Update API returns SWIFT details for paid transaction
	 */
	@Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber", description = "Verify Swift Update API returns SWIFT details successfully for paid transaction")
	public void verifySwiftUpdateApiReturnsSwiftDetailsForPaidTransaction() {

		Map<String, Object> requestBody = getSwiftTxnRequest();

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/swiftTxnDtls");

		logToReport(requestBody, response);

		// HTTP Status Code Validation
		Assert.assertEquals(response.getStatusCode(), 200);

		// Response Validation
		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertEquals(response.jsonPath().getString("MfxMessageStatus"), "SWIFT GENERATED");

		Assert.assertNotNull(response.jsonPath().getString("MfxswiftNo"));
		Assert.assertFalse(response.jsonPath().getString("MfxswiftNo").isEmpty());

		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC03: Verify Swift Update API with transaction that has no SWIFT details yet
	 */
	@Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber", description = "Verify Swift Update API returns success response even when SWIFT details are not generated yet")
	public void verifySwiftUpdateApiWithTransactionHavingNoSwiftDetailsYet() {

		Map<String, Object> requestBody = getSwiftTxnRequest();
		requestBody.put("txnRefNo", "KB10003330970013");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/swiftTxnDtls");

		logToReport(requestBody, response);

		// HTTP Status Code Validation
		Assert.assertEquals(response.getStatusCode(), 200);

		// Response Validation
		Assert.assertEquals(response.jsonPath().getString("status"), "S");

		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));
	}

	/**
	 * TC04: Verify Swift Update API with invalid token
	 */
	@Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber", description = "Verify Swift Update API returns error when authToken is invalid")
	public void verifySwiftUpdateApiWithInvalidToken() {

		Map<String, Object> requestBody = getSwiftTxnRequest();

		Response response = given().spec(requestSpec).header("authToken", "INVALID_TOKEN").body(requestBody)
				.post("/services/api/partner/swiftTxnDtls");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1007");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical error");
	}

	/**
	 * TC05: Verify Swift Update API with blank clientCode
	 */
	@Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber", description = "Verify Swift Update API returns error when clientCode is blank")
	public void verifySwiftUpdateApiWithBlankClientCode() {

		Map<String, Object> requestBody = getSwiftTxnRequest();
		requestBody.put("clientCode", "");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/swiftTxnDtls");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");
	}

	/**
	 * TC06: Verify Swift Update API with blank token
	 */
	@Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber", description = "Verify Swift Update API returns error when auth token is blank")
	public void verifySwiftUpdateApiWithBlankToken() {

		Map<String, Object> requestBody = getSwiftTxnRequest();

		Response response = given().spec(requestSpec).header("authToken", "").body(requestBody)
				.post("/services/api/partner/swiftTxnDtls");

		logToReport(requestBody, response);

		// Response Validation
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1007");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical error");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}
}