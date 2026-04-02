package stepdefinitions;

import java.time.Duration;
import java.util.HashMap;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import base.BaseTest;
import io.cucumber.java.AfterStep;
import io.cucumber.java.en.*;
import pages.EuroSignupPage;
import utils.ExcelUtils;
import utils.FakerUtils;

public class EuroSignupSteps {

    WebDriver driver = BaseTest.getDriver();
    EuroSignupPage signupPage;

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

    // ============================
    // 🔹 Load Application & Test Data
    // ============================
    @Given("user opens the axis remit page")
    public void user_opens_signup_page() throws Exception {

        String path = System.getProperty("user.dir") + "/src/test/resources/testdata/TestData.xlsx";

        ExcelUtils.loadExcel(path, "Sheet1");
        testData = ExcelUtils.getTestData(1);

        driver.get(testData.get("url"));
        signupPage = new EuroSignupPage(driver);
    }

    // ============================
    // 🔹 Button Actions
    // ============================
    @When("user clicks on signup button")
    public void user_clicks_signup() {
        signupPage.clickSignup();
    }

    @When("user clicks on register button")
    public void user_clicks_register() {
        signupPage.clickRegister();
    }

    @Then("user clicks on verify button")
    public void user_clicks_verify() {
        signupPage.clickVerify();
    }

    // ============================
    // 🔹 Dropdown Selections
    // ============================
    @When("user selects country")
    public void user_selects_country() {
        signupPage.selectCountry(testData.get("country"));
    }

    @When("user selects euro country")
    public void user_selects_euro_country() {
        signupPage.selectEuroCountry(testData.get("euroCountry"));
    }

    @When("user selects country code")
    public void user_selects_country_code() {
        System.out.println("Skipping country code selection step");
    }

    @When("user selects option")
    public void user_selects_option() {
        signupPage.selectOption(testData.get("option"));
    }

    @When("user selects nationality")
    public void user_selects_nationality() {
        signupPage.selectCitizenship(testData.get("nationality"));
    }

    // ============================
    // 🔹 Dynamic Inputs (Faker)
    // ============================
    @When("user enters name")
    public void user_enters_name() {
        String name = FakerUtils.getName();
        System.out.println("Name: " + name);
        signupPage.enterName(name);
    }

    @When("user enters email")
    public void user_enters_email() {
        String email = FakerUtils.getEmail();
        System.out.println("Email: " + email);
        signupPage.enterEmail(email);
    }

    @When("user enters password")
    public void user_enters_password() {
        String password = FakerUtils.getPassword();
        System.out.println("Password: " + password);
        signupPage.enterPassword(password);
    }

    @When("user enters mobile number")
    public void user_enters_mobile() {
        String mobile = FakerUtils.getMobile();
        System.out.println("Mobile: " + mobile);
        signupPage.enterMobileNo(mobile);
    }

    // ============================
    // 🔹 Checkbox Actions
    // ============================
    @When("user checks receive mail checkbox")
    public void user_checks_receive_mail_checkbox() {
        signupPage.setReceiveMail(true);
    }

    @When("user accepts terms and conditions")
    public void user_accepts_terms_and_conditions() {
        signupPage.setAgreeTerms(true);
        signupPage.setConsent(true);
    }

    // ============================
    // 🔹 OTP Handling
    // ============================
    @When("user enters OTP")
    public void user_enters_otp() {

        String otp = testData.get("otp").replace(".0", "");
        System.out.println("OTP: " + otp);

        signupPage.enterOtp(otp);
    }
    
    @Then("user should see registration success message")
    public void user_should_see_registration_success_message(String expectedMessage) {
    	String actualMessage = signupPage.getSuccessMessage();

        System.out.println("Actual Message: " + actualMessage);

        Assert.assertEquals(actualMessage.trim(), expectedMessage);
    }
}