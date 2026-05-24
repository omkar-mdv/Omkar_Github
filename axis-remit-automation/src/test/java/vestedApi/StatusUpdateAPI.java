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
public class StatusUpdateAPI extends ApiBaseTest {

	public static String generatedToken;
	public static String requestId;

	// -------------------- Common Methods --------------------

	private Map<String, Object> getGenerateTokenRequest() {

		// Generate single requestId
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

	private void logToReport(Map<String, Object> requestBody, Response response) {

		if (TestListener.test.get() != null) {
			TestListener.test.get().info("Request Body: " + requestBody);
			TestListener.test.get().info("Response Body: " + response.asPrettyString());
		}
	}

	// Reusable Assertion
	private void assertTxnStatus(Response response, String expectedStatus, String expectedCode) {

		String txnStatus = response.jsonPath().getString("txnStatus");
		String txnStatusCode = response.jsonPath().getString("txnStatusCode");

		Assert.assertNotNull(txnStatus, "txnStatus should not be null");
		Assert.assertNotNull(txnStatusCode, "txnStatusCode should not be null");

		Assert.assertEquals(txnStatus, expectedStatus, "Unexpected txnStatus");
		Assert.assertEquals(txnStatusCode, expectedCode, "Unexpected txnStatusCode");
	}

	private Response hitStatusUpdate(String txnRefNo) {

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("groupId", "KB");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("txnRefNo", txnRefNo);

		// Same requestId generated in Generate Token API
		requestBody.put("requestId", requestId);

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody)
				.post("/services/api/partner/statusUpdate");

		logToReport(requestBody, response);

		return response;
	}

	// -------------------- Test Cases --------------------

	/**
	 * TC01: Verify Generate Token API
	 */
	@Test(description = "Verify token is generated successfully")
	public void verifyGenerateTokenApiReturnsSuccessWithValidRequest() {

		Response response = given().spec(requestSpec).body(getGenerateTokenRequest())
				.post("/services/api/partner/generateToken");

		generatedToken = response.jsonPath().getString("token");

		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertNotNull(generatedToken);
	}

	/**
	 * TC02: Verify Status Update API returns correct transaction status
	 */
	@Test(dependsOnMethods = "verifyGenerateTokenApiReturnsSuccessWithValidRequest", description = "Verify Status Update API returns correct transaction status")
	public void verifyStatusUpdateApiReturnsCorrectTransactionStatus() {

		String txnRefNo = config.getProperty("txnRefNo");

		Response response = hitStatusUpdate(txnRefNo);

		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertEquals(response.jsonPath().getString("txnRefNo"), txnRefNo);

		assertTxnStatus(response, "CRN Updated", "153");
	}

	/**
	 * TC03: Verify Status Update API returns txnStatusCode 101
	 */
	@Test(dependsOnMethods = "verifyGenerateTokenApiReturnsSuccessWithValidRequest", description = "Verify Status Update API returns status 101")
	public void verifyStatusUpdateApiReturnsTxnStatus101ForBookedTransaction() {

		String txnRefNo = config.getProperty("txnRefNo101");

		Response response = hitStatusUpdate(txnRefNo);

		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertEquals(response.jsonPath().getString("txnRefNo"), txnRefNo);

		assertTxnStatus(response, "Transaction Booked", "101");
	}

	/**
	 * TC04: Verify Status Update API returns txnStatusCode 153
	 */
	@Test(dependsOnMethods = "verifyGenerateTokenApiReturnsSuccessWithValidRequest", description = "Verify Status Update API returns status 153")
	public void verifyStatusUpdateApiReturnsTxnStatus153ForCrnUpdatedTransaction() {

		String txnRefNo = config.getProperty("txnRefNo153");

		Response response = hitStatusUpdate(txnRefNo);

		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertEquals(response.jsonPath().getString("txnRefNo"), txnRefNo);

		assertTxnStatus(response, "CRN Updated", "153");
	}

	/**
	 * TC05: Verify Status Update API returns txnStatusCode 204
	 */
	@Test(dependsOnMethods = "verifyGenerateTokenApiReturnsSuccessWithValidRequest", description = "Verify Status Update API returns status 204")
	public void verifyStatusUpdateApiReturnsTxnStatus204ForMoneyDeliveredTransaction() {

		String txnRefNo = config.getProperty("txnRefNo204");

		Response response = hitStatusUpdate(txnRefNo);

		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertEquals(response.jsonPath().getString("txnRefNo"), txnRefNo);

		assertTxnStatus(response, "Money Delivered", "204");
	}

	/**
	 * TC06: Verify Status Update API returns txnStatusCode 306
	 */
	@Test(dependsOnMethods = "verifyGenerateTokenApiReturnsSuccessWithValidRequest", description = "Verify Status Update API returns status 306")
	public void verifyStatusUpdateApiReturnsTxnStatus306ForCredenceRejectedTransaction() {

		String txnRefNo = config.getProperty("txnRefNo306");

		Response response = hitStatusUpdate(txnRefNo);

		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertEquals(response.jsonPath().getString("txnRefNo"), txnRefNo);

		assertTxnStatus(response, "Transaction Rejected by Credence", "306");
	}

	/**
	 * TC07: Verify Status Update API returns error 1008
	 */
	@Test(dependsOnMethods = "verifyGenerateTokenApiReturnsSuccessWithValidRequest", description = "Verify Status Update API returns error 1008")
	public void verifyStatusUpdateApiReturnsError1008WhenTxnRefNoIsInvalid() {

		String txnRefNo = config.getProperty("txnRefNoInvalid");

		Response response = hitStatusUpdate(txnRefNo);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");

		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");
	}

	/**
	 * TC08: Verify Status Update API returns error 1007
	 */
	@Test(dependsOnMethods = "verifyGenerateTokenApiReturnsSuccessWithValidRequest", description = "Verify Status Update API returns error 1007 when auth token is invalid")
	public void verifyStatusUpdateApiReturnsError1007WhenAuthTokenIsInvalid() {

		String txnRefNo = config.getProperty("txnRefNo");

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("groupId", "KB");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("txnRefNo", txnRefNo);

		// Same requestId used here also
		requestBody.put("requestId", requestId);

		Response response = given().spec(requestSpec).header("authToken", "INVALID_TOKEN").body(requestBody)
				.post("/services/api/partner/statusUpdate");

		logToReport(requestBody, response);

		Assert.assertEquals(response.jsonPath().getString("status"), "F");

		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1007");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical error");
	}
}