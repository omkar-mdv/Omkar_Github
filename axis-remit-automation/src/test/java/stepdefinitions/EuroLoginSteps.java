package stepdefinitions;

import java.util.HashMap;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;

import base.BaseTest;
import io.cucumber.java.AfterStep;
import io.cucumber.java.en.*;
import pages.EuroLoginPage;
import pages.EuroSignupPage;
import utils.ExcelUtils;
import utils.TestDataStore;

public class EuroLoginSteps {

	WebDriver driver = BaseTest.getDriver();
    EuroSignupPage signupPage;
    EuroLoginPage loginPage;
    
    // Store Excel Data
    HashMap<String, String> testData;

    // ============================
    // 🔹 Global Wait After Each Step
    // ============================
    @AfterStep
    public void addDelay() {
        try {
            Thread.sleep(2000); // 2 seconds delay after every step
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // ================= STEPS ================= //

    @When("user navigates to sign in page")
    public void user_navigates_to_sign_in_page() throws Exception {
    	
    	String path = System.getProperty("user.dir") + "/src/test/resources/testdata/TestData.xlsx";

        ExcelUtils.loadExcel(path, "Sheet1");
        testData = ExcelUtils.getTestData(1);

        String loginUrl = testData.get("loginUrl");
        driver.get(loginUrl);
    }

    @When("user enters registered email")
    public void user_enters_registered_email() {
        loginPage.enterEmail(TestDataStore.email);
    }

    @When("user enters registered password")
    public void user_enters_registered_password() {
        loginPage.enterPassword(TestDataStore.password);
    }

    @When("user clicks on login button")
    public void user_clicks_on_login_button() {
        loginPage.clickLogin();
    }

    @Then("user should receive OTP")
    public void user_should_receive_otp() {
        System.out.println("OTP Sent to registered email");
    }

    @When("user enters OTP for login")
    public void user_enters_otp_for_login() {
        signupPage.enterOtp("123456"); // replace with dynamic OTP if needed
    }

    @When("user clicks on verify button for login")
    public void user_clicks_on_verify_button_for_login() {
        signupPage.clickVerify();
    }

    @Then("user should be logged in successfully")
    public void user_should_be_logged_in_successfully() {

        String message = signupPage.getSuccessMessage();

        Assert.assertTrue("Login failed!", message.contains("successfully"));
    }
}