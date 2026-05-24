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
public class SendVerifyOtpAPI extends ApiBaseTest {

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
        requestBody.put("panNo", "");
        requestBody.put("channel", "WEB");
        requestBody.put("groupId", "KB");
        requestBody.put("clientCode", "VESTED");

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

        Response response = given().spec(requestSpec).body(requestBody)
                .post("/services/api/partner/generateToken");

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
     * TC02: Verify Send OTP API triggers SMS
     */
    @Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber",
          description = "Verify OTP is sent successfully to customer's registered mobile number")
    public void verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber() {

        Map<String, Object> requestBody = getOtpRequest();

        Response response = given().spec(requestSpec)
                .header("authToken", generatedToken)
                .body(requestBody)
                .post("/services/api/partner/sendOTP");

        logToReport(requestBody, response);

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.jsonPath().getString("status"), "S");
    }

    /**
     * TC03: Verify OTP API with incorrect OTP
     */
    @Test(dependsOnMethods = "verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber",
          description = "Verify API returns error when incorrect OTP is provided")
    public void verifyOtpApiWithIncorrectOtp() {

        Map<String, Object> requestBody = getOtpRequest();
        requestBody.put("otp", "999999");

        Response response = given().spec(requestSpec)
                .header("authToken", generatedToken)
                .body(requestBody)
                .post("/services/api/partner/verifyOTP");

        logToReport(requestBody, response);

        Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1009");
        Assert.assertEquals(response.jsonPath().getString("errorDescription"), "OTP is incorrect");
    }

//    /**
//     * TC04: Verify OTP API with correct OTP
//     */
//    @Test(dependsOnMethods = "verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber",
//          description = "Verify API successfully validates correct OTP")
//    public void verifyOtpApiWithCorrectSixDigitOtp() {
//
//        Map<String, Object> requestBody = getOtpRequest();
//        requestBody.put("otp", "123456");
//
//        Response response = given().spec(requestSpec)
//                .header("authToken", generatedToken)
//                .body(requestBody)
//                .post("/services/api/partner/verifyOTP");
//
//        logToReport(requestBody, response);
//
//        Assert.assertEquals(response.jsonPath().getString("status"), "S");
//    }

    /**
     * TC05: Verify OTP API with less than 6 digits
     */
    @Test(dependsOnMethods = "verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber",
          description = "Verify API returns error when OTP is less than 6 digits")
    public void verifyOtpApiWithLessThanSixDigits() {

        Map<String, Object> requestBody = getOtpRequest();
        requestBody.put("otp", "12345");

        Response response = given().spec(requestSpec)
                .header("authToken", generatedToken)
                .body(requestBody)
                .post("/services/api/partner/verifyOTP");

        logToReport(requestBody, response);

        Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
    }

    /**
     * TC06: Verify OTP API with more than 6 digits
     */
    @Test(dependsOnMethods = "verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber",
          description = "Verify API returns error when OTP is more than 6 digits")
    public void verifyOtpApiWithMoreThanSixDigits() {

        Map<String, Object> requestBody = getOtpRequest();
        requestBody.put("otp", "1234567");

        Response response = given().spec(requestSpec)
                .header("authToken", generatedToken)
                .body(requestBody)
                .post("/services/api/partner/verifyOTP");

        logToReport(requestBody, response);

        Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
    }

    /**
     * TC07: Verify OTP API with alphabets
     */
    @Test(dependsOnMethods = "verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber",
          description = "Verify API returns error when OTP contains alphabetic characters")
    public void verifyOtpApiWithAlphabeticCharactersInOtpField() {

        Map<String, Object> requestBody = getOtpRequest();
        requestBody.put("otp", "ABCDEF");

        Response response = given().spec(requestSpec)
                .header("authToken", generatedToken)
                .body(requestBody)
                .post("/services/api/partner/verifyOTP");

        logToReport(requestBody, response);

        Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
    }

    /**
     * TC08: Verify OTP API without auth token
     */
    @Test(dependsOnMethods = "verifySendOtpApiTriggersSmsToCustomerRegisteredMobileNumber",
          description = "Verify API returns error when authToken is missing")
    public void verifyOtpApiWithoutAuthToken() {

        Map<String, Object> requestBody = getOtpRequest();
        requestBody.put("otp", "123456");

        Response response = given().spec(requestSpec)
                .body(requestBody)
                .post("/services/api/partner/verifyOTP");

        logToReport(requestBody, response);

        Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1007");
    }

    /**
     * TC09: Verify Send OTP API with missing CRN
     */
    @Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber",
          description = "Verify API returns error when CRN is missing in Send OTP request")
    public void verifySendOtpApiWithMissingMandatoryCRNField() {

        Map<String, Object> requestBody = getOtpRequest();
        requestBody.put("crn", "");

        Response response = given().spec(requestSpec)
                .header("authToken", generatedToken)
                .body(requestBody)
                .post("/services/api/partner/sendOTP");

        logToReport(requestBody, response);

        Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1008");
    }

    /**
     * TC10: Verify Send OTP API with invalid auth token
     */
    @Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber",
          description = "Verify API returns error when authToken is invalid or expired")
    public void verifySendOtpApiWithInvalidExpiredAuthToken() {

        Map<String, Object> requestBody = getOtpRequest();

        Response response = given().spec(requestSpec)
                .header("authToken", "INVALID_EXPIRED_TOKEN")
                .body(requestBody)
                .post("/services/api/partner/sendOTP");

        logToReport(requestBody, response);

        Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1007");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical error");
    }

    /**
     * TC11: Verify Send OTP API without auth token
     */
    @Test(dependsOnMethods = "verifyGenerateTokenApiWithValidCRNAndAccountNumber",
          description = "Verify API returns error when authToken is not provided in header")
    public void verifySendOtpApiWithoutAuthTokenInHeader() {

        Map<String, Object> requestBody = getOtpRequest();

        Response response = given().spec(requestSpec)
                .body(requestBody)
                .post("/services/api/partner/sendOTP");

        logToReport(requestBody, response);

        Assert.assertEquals(response.jsonPath().getString("status"), "F");
		Assert.assertEquals(response.jsonPath().getString("errCode"), "ERR1007");
		Assert.assertEquals(response.jsonPath().getString("errorDescription"), "Technical error");
    }
}