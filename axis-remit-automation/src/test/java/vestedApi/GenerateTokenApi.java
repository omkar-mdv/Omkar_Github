package vestedApi;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import io.restassured.builder.ResponseSpecBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import base.ApiBaseTest;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class GenerateTokenApi extends ApiBaseTest {

	private RequestSpecification requestSpec;
	private ResponseSpecification responseSpec;
	public static String generatedToken;

	@BeforeClass
	public void setup() {
		RestAssured.baseURI = "https://kb.remit.in";

		requestSpec = given().contentType(ContentType.JSON).accept(ContentType.JSON);

		responseSpec = new ResponseSpecBuilder().expectStatusCode(200).expectContentType(ContentType.JSON).build();
	}

	@Test (priority = 1)
	public void verifyGenerateTokenApiWithValidCRNAndAccountNumberForEligibleETBCustomer() {

		// Request Body
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "REQTEST002");
		requestBody.put("crn", "100759017");
		requestBody.put("accNo", "7150157172");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");

		// Execute API
		Response response = requestSpec.body(requestBody).when().post("/services/api/partner/generateToken").then()
				.log().ifValidationFails().extract().response();

		// HTTP Status Code Validation
		Assert.assertEquals(response.getStatusCode(), 200);

		// Response Body Validations
		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));

		// Token Validation
		String token = response.jsonPath().getString("token");
		Assert.assertNotNull(token, "Token should not be null");
		Assert.assertFalse(token.isEmpty(), "Token should not be empty");
	}

	@Test
	public void verifyGenerateTokenApiWithEmptyCRN() {

		// Request body with EMPTY CRN
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "REQTEST002");
		requestBody.put("crn", ""); // Mandatory field sent empty
		requestBody.put("accNo", "7150157172");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");

		// Execute API
		Response response = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(requestBody).when()
				.post("/services/api/partner/generateToken").then().log().ifValidationFails().extract().response();

		// HTTP Status Code Validation
		Assert.assertEquals(response.getStatusCode(), 200, "HTTP status should be 200 for business validation failure");

		// Response Body Validations
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		// Token should be empty on failure
		Assert.assertEquals(response.jsonPath().getString("token"), "");

		// Success & Failure flags
		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	@Test
	public void verifyGenerateTokenApiWhenAadharLinkIsN() {

		// Request Body
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "REQTEST002");
		requestBody.put("crn", "27374059");
		requestBody.put("accNo", "04221040003886");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");

		// Execute API
		Response response = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(requestBody).when()
				.post("/services/api/partner/generateToken").then().log().ifValidationFails().extract().response();

		// HTTP Status Code Validation
		Assert.assertEquals(response.getStatusCode(), 200, "HTTP status should be 200 for business validation failure");

		// Response Body Validations
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR7004");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Business Decline");

		// Token should be empty on failure
		Assert.assertEquals(response.jsonPath().getString("token"), "");

		// Success & Failure Flags
		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	@Test
	public void verifyGenerateTokenApiWithMissingMandatoryFieldCRN() {

		// Request Body with missing CRN
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "REQTEST002");
		requestBody.put("crn", ""); // Mandatory field missing
		requestBody.put("accNo", "7150157172");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");

		// Execute API
		Response response = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(requestBody).when()
				.post("/services/api/partner/generateToken").then().log().ifValidationFails().extract().response();

		// HTTP Status Code Validation
		Assert.assertEquals(response.getStatusCode(), 200, "HTTP status should be 200 for business validation failure");

		// Response Body Validations
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		// Token should be empty on failure
		Assert.assertEquals(response.jsonPath().getString("token"), "");

		// Success & Failure Flags
		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	@Test
	public void verifyGenerateTokenApiWithMissingMandatoryFieldAccNo() {

		// Request Body with missing accNo
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "REQTEST002");
		requestBody.put("crn", "100759017");
		requestBody.put("accNo", ""); // Mandatory field missing
		requestBody.put("clientCode", "VESTED");
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");

		// Execute API
		Response response = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(requestBody).when()
				.post("/services/api/partner/generateToken").then().log().ifValidationFails().extract().response();

		// HTTP Status Code Validation
		Assert.assertEquals(response.getStatusCode(), 200, "HTTP status should be 200 for business validation failure");

		// Response Body Validations
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		// Token should be empty on failure
		Assert.assertEquals(response.jsonPath().getString("token"), "");

		// Success & Failure Flags
		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	@Test
	public void verifyGenerateTokenApiWithMissingClientCode() {

		// Request Body with missing clientCode
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "REQTEST002");
		requestBody.put("crn", "100759017");
		requestBody.put("accNo", "7150157172");
		requestBody.put("clientCode", ""); // Mandatory field missing
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");

		// Execute API
		Response response = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(requestBody).when()
				.post("/services/api/partner/generateToken").then().log().ifValidationFails().extract().response();

		// HTTP Status Code Validation
		Assert.assertEquals(response.getStatusCode(), 200, "HTTP status should be 200 for business validation failure");

		// Response Body Validations
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		// Token should be empty on failure
		Assert.assertEquals(response.jsonPath().getString("token"), "");

		// Success & Failure Flags
		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	@Test
	public void verifyGenerateTokenApiWithCRNAboveMaximumLength() {

		// Request Body with CRN above maximum length
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "REQTEST002");
		requestBody.put("crn", "1234567890123456");
		requestBody.put("accNo", "7150157172");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");

		// Execute API
		Response response = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(requestBody).when()
				.post("/services/api/partner/generateToken").then().log().ifValidationFails().extract().response();

		// HTTP Status Code Validation
		Assert.assertEquals(response.getStatusCode(), 200, "HTTP status should be 200 for business validation failure");

		// Response Body Validations
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		// Token should be empty on failure
		Assert.assertEquals(response.jsonPath().getString("token"), "");

		// Success & Failure Flags
		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	@Test
	public void verifyGenerateTokenApiWithAlphabeticCharactersInCRN() {

		// Request Body with alphabetic characters in CRN
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "REQTEST002");
		requestBody.put("crn", "ABCD59017");
		requestBody.put("accNo", "7150157172");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");

		// Execute API
		Response response = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(requestBody).when()
				.post("/services/api/partner/generateToken").then().log().ifValidationFails().extract().response();

		// HTTP Status Code Validation
		Assert.assertEquals(response.getStatusCode(), 200, "HTTP status should be 200 for business validation failure");

		// Response Body Validations
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		// Token should be empty on failure
		Assert.assertEquals(response.jsonPath().getString("token"), "");

		// Success & Failure Flags
		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	@Test
	public void verifyGenerateTokenApiWithSpecialCharactersInAccNo() {

		// Request Body with special characters in accNo
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "REQTEST002");
		requestBody.put("crn", "100759017");
		requestBody.put("accNo", "0958!@#$%");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");

		// Execute API
		Response response = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(requestBody).when()
				.post("/services/api/partner/generateToken").then().log().ifValidationFails().extract().response();

		// HTTP Status Code Validation
		Assert.assertEquals(response.getStatusCode(), 200, "HTTP status should be 200 for business validation failure");

		// Response Body Validations
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		// Token should be empty on failure
		Assert.assertEquals(response.jsonPath().getString("token"), "");

		// Success & Failure Flags
		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	@Test
	public void verifyGenerateTokenApiValidatesCustomerViaCRNApiAndAccountInquiryApi() {

		// Request Body
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "REQTEST002");
		requestBody.put("crn", "100759017");
		requestBody.put("accNo", "7150157172");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");

		// Execute API
		Response response = requestSpec.body(requestBody).when().post("/services/api/partner/generateToken").then()
				.log().ifValidationFails().extract().response();

		// HTTP Status Code Validation
		Assert.assertEquals(response.getStatusCode(), 200);

		// Response Body Validations
		Assert.assertEquals(response.jsonPath().getString("status"), "S");
		Assert.assertTrue(response.jsonPath().getBoolean("success"));
		Assert.assertFalse(response.jsonPath().getBoolean("failure"));

		// Token validation
		String token = response.jsonPath().getString("token");
		Assert.assertNotNull(token, "Token should not be null");
		Assert.assertFalse(token.isEmpty(), "Token should not be empty");
	}

	@Test
	public void verifyGenerateTokenApiWhenCustomerCRNStatusIsBlacklisted() {

		// Request Body
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "REQTEST002");
		requestBody.put("crn", "8593560");
		requestBody.put("accNo", "08160140009332");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");

		// Execute API
		Response response = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(requestBody).when()
				.post("/services/api/partner/generateToken").then().log().ifValidationFails().extract().response();

		// HTTP Status Code Validation
		Assert.assertEquals(response.getStatusCode(), 200, "HTTP status should be 200 for business validation failure");

		// Response Body Validations
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR7002");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Business Decline");

		// Token should be empty on failure
		Assert.assertEquals(response.jsonPath().getString("token"), "");

		// Success & Failure Flags
		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}

	@Test
	public void verifyGenerateTokenApiWithCRNBelowMinimumLength() {

		// Request Body with CRN below minimum length
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("requestId", "REQTEST002");
		requestBody.put("crn", "1234567");
		requestBody.put("accNo", "7150157172");
		requestBody.put("clientCode", "VESTED");
		requestBody.put("groupId", "KB");
		requestBody.put("channel", "WEB");

		// Execute API
		Response response = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(requestBody).when()
				.post("/services/api/partner/generateToken").then().log().ifValidationFails().extract().response();

		// HTTP Status Code Validation
		Assert.assertEquals(response.getStatusCode(), 200, "HTTP status should be 200 for business validation failure");

		// Response Body Validations
		Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical Decline");

		// Token should be empty on failure
		Assert.assertEquals(response.jsonPath().getString("token"), "");

		// Success & Failure Flags
		Assert.assertFalse(response.jsonPath().getBoolean("success"));
		Assert.assertTrue(response.jsonPath().getBoolean("failure"));
	}
}
