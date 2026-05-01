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

@Listeners(listeners.TestListener.class)
public class QuoteApi extends ApiBaseTest {

	public static String generatedToken;
	public static String requestId;

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

		return requestBody;
	}

	private void logToReport(Map<String, Object> requestBody, Response response) {
		if (TestListener.test.get() != null) {
			TestListener.test.get().info("Request Body: " + requestBody);
			TestListener.test.get().info("Response Body: " + response.asPrettyString());
		}
	}

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

	@Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber")
	public void verifyQuoteApiReturnsFxRateChargesTcsForValidTransaction() {

		Map<String, Object> requestBody = getQuoteRequest();

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody).when()
				.post("/services/api/partner/getQuote").then().log().all().extract().response();

		logToReport(requestBody, response);

		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));

		Map<String, Object> responseMap = response.jsonPath().getMap("$");
		Map<String, Object> quoteDetails = response.jsonPath().getMap("getQuoteDetails");
		Map<String, Object> transactionDetails = response.jsonPath().getMap("transactionDetails");

		TestListener.test.get().info("Validating response fields...");

		// Root Level
		Assert.assertTrue(responseMap.containsKey("genbill voucher no"), "genbill voucher no field is missing");

		// getQuoteDetails
		Assert.assertTrue(quoteDetails.containsKey("quoteId"), "quoteId field is missing in getQuoteDetails");

		// transactionDetails
		Assert.assertTrue(transactionDetails.containsKey("rate"), "rate field is missing in transactionDetails");

		Assert.assertTrue(transactionDetails.containsKey("charges"), "charges field is missing in transactionDetails");

		Assert.assertTrue(transactionDetails.containsKey("tcsTaxAmount"),
				"tcsTaxAmount field is missing in transactionDetails");

		Assert.assertTrue(transactionDetails.containsKey("lcyamt"), "lcyamt field is missing in transactionDetails");

		Assert.assertTrue(transactionDetails.containsKey("finalSendAmountINR"),
				"finalSendAmountINR field is missing in transactionDetails");
	}

	@Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber")
	public void verifyQuoteApiValidatesPurposeAgainstKrPurposeList() {

		Map<String, Object> requestBody = getQuoteRequest();
		requestBody.put("purposedescription", "LRS GIFT"); // Only purpose changed

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody).when()
				.post("/services/api/partner/getQuote").then().log().all().extract().response();

		logToReport(requestBody, response);

		// HTTP Status Code
		Assert.assertEquals(response.getStatusCode(), 200);

		// Response Validation
		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));
	}

	@Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber")
	public void verifyQuoteApiReturnsError1003WhenPurposeMismatch() {

		Map<String, Object> requestBody = getQuoteRequest();
		requestBody.put("purposedescription", "INVALID PURPOSE");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody).when()
				.post("/services/api/partner/getQuote").then().log().all().extract().response();

		logToReport(requestBody, response);

		// Response Validation
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1003");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Purpose mismatch");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	@Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber")
	public void verifyQuoteApiReturnsError1008ForInvalidFieldFormat() {

		Map<String, Object> requestBody = getQuoteRequest();
		requestBody.put("fc_amt", "ABC");

		Response response = given().spec(requestSpec).header("authToken", generatedToken).body(requestBody).when()
				.post("/services/api/partner/getQuote").then().log().all().extract().response();

		logToReport(requestBody, response);

		// HTTP Status Code
		Assert.assertEquals(response.getStatusCode(), 400);

		// Response Validation
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}
}