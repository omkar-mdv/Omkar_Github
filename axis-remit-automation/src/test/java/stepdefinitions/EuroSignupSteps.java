package stepdefinitions;

import java.util.HashMap;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import base.BaseTest;
import io.cucumber.java.en.*;
import pages.EuroSignupPage;
import utils.ExcelUtils;
import utils.FakerUtils;

/**
 * ============================================================ 
 * Class Name :
 * EuroSignupSteps Description : Step Definitions for Euro Signup Feature Author
 * : Omkar Framework : Cucumber + Selenium + POM + Excel + Faker
 *
 * Data Strategy: - Static Data → Excel (TestData.xlsx) - Dynamic Data → Faker
 * (Name, Email, Mobile, Password)
 *
 * Excel Location: src/test/resources/testdata/TestData.xlsx
 *
 * Sheet Format: Row 0 → Headers Row 1 → Test Data
 *
 * ============================================================
 */

public class EuroSignupSteps {

	WebDriver driver = BaseTest.getDriver();
	EuroSignupPage signupPage;

	// Store Excel Data
	HashMap<String, String> testData;

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
	// 🔹 Button Actions (From Excel)
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
	// 🔹 Scroll Action
	// ============================
	@When("user scrolls down")
	public void user_scrolls_down() {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,500)");
	}

	// ============================
	// 🔹 Dropdown Selections (Excel)
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
	    // Intentionally skipped
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
		signupPage.enterName(FakerUtils.getName());
	}

	@When("user enters email")
	public void user_enters_email() {
		signupPage.enterEmail(FakerUtils.getEmail());
	}

	@When("user enters password")
	public void user_enters_password() {
		signupPage.enterPassword(FakerUtils.getPassword());
	}

	@When("user enters mobile number")
	public void user_enters_mobile() {
		signupPage.enterMobileNo(FakerUtils.getMobile());
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
		signupPage.enterOtp(testData.get("otp"));
	}
}