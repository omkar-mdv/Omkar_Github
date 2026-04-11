package stepdefinitions;

import java.util.HashMap;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import base.BaseTest;
import io.cucumber.java.AfterStep;
import io.cucumber.java.en.*;
import pages.EuroLoginPage;
import pages.EuroSignupPage;
import utils.ExcelUtils;
import utils.FakerUtils;
import utils.ConfigReader;

public class EuroSignupLoginSteps {

	WebDriver driver = BaseTest.getDriver();
	EuroSignupPage signupPage;
	EuroLoginPage loginPage;

	// Test data from Excel
	HashMap<String, String> testData;

	// ✅ Store signup credentials for reuse in login\
	private String registeredEmail;
	private String registeredPassword;
	private String registeredName;

	// ============================
	// 🔹 Global Wait
	// ============================
	@AfterStep
	public void addDelay() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// ============================
	// 🔹 Launch Application
	// ============================
	@Given("user opens the axis remit page")
	public void user_opens_the_axis_remit_page() throws Exception {
		String path = System.getProperty("user.dir") + "/src/test/resources/testdata/TestData.xlsx";

		ExcelUtils.loadExcel(path, "Sheet1");
		testData = ExcelUtils.getTestData(1);

		driver.get(testData.get("baseUrl"));
		signupPage = new EuroSignupPage(driver);
		loginPage = new EuroLoginPage(driver);
	}

//	Fetched from config.properties
//	@Given("user opens the axis remit page")
//	public void user_opens_the_axis_remit_page() {
//
//	    driver.get(ConfigReader.get("base.url"));
//
//	    signupPage = new EuroSignupPage(driver);
//	    loginPage = new EuroLoginPage(driver);
//	}

	// ============================
	// 🔹 SIGNUP FLOW
	// ============================
	@When("user clicks on signup button")
	public void user_clicks_on_signup_button() {
		signupPage.clickSignup();
	}

	@When("user selects country")
	public void user_selects_country() {
		signupPage.selectCountry(testData.get("country"));
	}

//	Fetched from config.properties
//	@When("user selects country")
//	public void user_selects_country() {
//		signupPage.selectCountry(ConfigReader.get("country"));
//	}

	@When("user selects euro country")
	public void user_selects_euro_country() {
		signupPage.selectEuroCountry(testData.get("euroCountry"));
	}

//	Fetched from config.properties
//	@When("user selects euro country")
//	public void user_selects_euro_country() {
//		signupPage.selectEuroCountry(ConfigReader.get("euroCountry"));
//	}

	@When("user enters name")
	public void user_enters_name() {
		registeredName = FakerUtils.getName().toUpperCase();
		System.out.println("Registered Name: " + registeredName);
		signupPage.enterName(registeredName);
	}

	@When("user enters email")
	public void user_enters_email() {
		registeredEmail = FakerUtils.getEmail();
		System.out.println("Registered Email: " + registeredEmail);
		signupPage.enterEmail(registeredEmail);
	}

	@When("user enters password")
	public void user_enters_password() {
		registeredPassword = FakerUtils.getPassword();
		System.out.println("Registered Password: " + registeredPassword);
		signupPage.enterPassword(registeredPassword);
	}

	@When("user selects country code")
	public void user_selects_country_code() {
		System.out.println("Country code already selected by default");
	}

	@When("user enters mobile number")
	public void user_enters_mobile_number() {
		signupPage.enterMobileNo(FakerUtils.getMobile());
	}

	@When("user selects option")
	public void user_selects_option() {
		signupPage.selectOption(testData.get("option"));
	}

//	Fetched from config.properties
//	@When("user selects option")
//	public void user_selects_option() {
//		signupPage.selectOption(ConfigReader.get("option"));
//	}

	@When("user selects nationality")
	public void user_selects_nationality() {
		signupPage.selectCitizenship(testData.get("nationality"));
	}

//	Fetched from config.properties
//	@When("user selects nationality")
//	public void user_selects_nationality() {
//		signupPage.selectCitizenship(ConfigReader.get("nationality"));
//	}

	@When("user checks receive mail checkbox")
	public void user_checks_receive_mail_checkbox() {
		signupPage.setReceiveMail(true);
	}

	@When("user accepts terms and conditions")
	public void user_accepts_terms_and_conditions() {
		signupPage.setAgreeTerms(true);
		signupPage.setConsent(true);
	}

	@When("user clicks on register button")
	public void user_clicks_on_register_button() {
		signupPage.clickRegister();
	}

	@When("user enters OTP for signup")
	public void user_enters_otp_for_signup() {
		String otp = testData.get("otp").replace(".0", "");
		signupPage.enterOtp(otp);
	}

//	Fetched from config.properties
//	@When("user enters OTP for signup")
//	public void user_enters_otp_for_signup() {
//		signupPage.enterOtp(ConfigReader.get("otp"));
//	}

	@When("user clicks on verify button for signup")
	public void user_clicks_on_verify_button_for_signup() {
		signupPage.clickVerify();
	}

	@Then("user should see registration success message")
	public void user_should_see_registration_success_message() {
		String message = signupPage.getSuccessMessage();
		Assert.assertEquals(message.trim(), "Thank you. You are successfully registered.");
	}

	// ============================
	// 🔹 LOGIN FLOW
	// ============================
	@When("user navigates to sign in page")
	public void user_navigates_to_sign_in_page() {
		driver.get(testData.get("loginUrl"));
	}

//	Fetched from config.properties
//	@When("user navigates to sign in page")
//	public void user_navigates_to_sign_in_page() {
//		driver.get(ConfigReader.get("login.url"));
//	}

	@When("user enters registered email")
	public void user_enters_registered_email() {
		loginPage.enterEmail(registeredEmail);
	}

	@When("user enters registered password")
	public void user_enters_registered_password() {
		loginPage.enterPassword(registeredPassword);
	}

	@When("user clicks on login button")
	public void user_clicks_on_login_button() {
		loginPage.clickLogin();
	}

	@Then("user should receive OTP")
	public void user_should_receive_otp() {

		String actualMessage = loginPage.getOtpSentToastMessage().trim();
		System.out.println("OTP Message: " + actualMessage);

		Assert.assertTrue(actualMessage.contains("OTP has been sent"), "OTP sent notification not displayed!");
	}

	@When("user enters OTP for login")
	public void user_enters_otp_for_login() {
		signupPage.enterOtp(testData.get("otp").replace(".0", ""));
	}

//	Fetched from config.properties
//	@When("user enters OTP for login")
//	public void user_enters_otp_for_login() {
//		signupPage.enterOtp(ConfigReader.get("otp"));
//	}

	@When("user clicks on verify button for login")
	public void user_clicks_on_verify_button_for_login() {
		signupPage.clickVerify();
	}

	@Then("user should be logged in successfully")
	public void user_should_be_logged_in_successfully() {

		String actualMessage = signupPage.getSuccessMessage();
		System.out.println("Login Message: " + actualMessage);

		// Example UI message: "WELCOME VIRAT KOHLI"
		String expectedMessage = "WELCOME " + registeredName;

		Assert.assertEquals(actualMessage.trim(), expectedMessage, "Login welcome message mismatch!");
	}

}