package stepdefinitions;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import base.BaseTest;
import io.cucumber.java.en.*;
import pages.EuroSignupPage;

public class EuroSignupSteps {

	WebDriver driver = BaseTest.getDriver();
	EuroSignupPage signupPage;

	@Given("user opens the axis remit page {string}")
	public void user_opens_signup_page(String url) {
		driver.get(url);
		signupPage = new EuroSignupPage(driver);
	}

//    @When("user clicks on {string}")
//    public void user_clicks_on(String button) {
//
//        if (button.equalsIgnoreCase("SIGN UP")) {
//            signupPage.clickSignup();
//        } else {
//            throw new IllegalArgumentException("Only SIGN UP is supported. Passed: " + button);
//        }
//    }

	@When("user clicks on {string}")
	public void user_clicks_on(String button) {

		switch (button.toUpperCase()) {
		case "SIGN UP":
			signupPage.clickSignup();
			break;

		case "REGISTER NOW":
			signupPage.clickRegister();
			break;

		case "VERIFY ACCOUNT":
			signupPage.clickVerify();
			break;

		default:
			throw new IllegalArgumentException("Invalid button: " + button);
		}
	}
	
//	@When("user scrolls down")
//	public void user_scrolls_down() {
//		WebElement verifyBtn = driver.findElement(By.xpath("//button[contains(text(),'VERIFY')]"));
//
//		JavascriptExecutor js = (JavascriptExecutor) driver;
//		js.executeScript("arguments[0].scrollIntoView(true);", verifyBtn);
//	}

	@When("user selects country {string}")
	public void user_selects_country(String country) {
		signupPage.selectCountry(country);
	}
	
	@When("user scrolls down")
	public void user_scrolls_down() {
	    JavascriptExecutor js = (JavascriptExecutor) driver;
	    js.executeScript("window.scrollBy(0,500)");
	}

	@When("user selects euro country {string}")
	public void user_selects_euro_country(String euroCountry) {
		signupPage.selectEuroCountry(euroCountry);
	}

	@When("user enters name {string}")
	public void user_enters_name(String name) {
		signupPage.enterName(name);
	}

	@When("user enters email {string}")
	public void user_enters_email(String email) {
		signupPage.enterEmail(email);
	}

	@When("user enters password {string}")
	public void user_enters_password(String password) {
		signupPage.enterPassword(password);
	}
	
//	@When("user selects country code {string}")
//	public void user_selects_country_code(String code) {
//		signupPage.selectMobileCode(code);
//	}
	
	@When("user selects country code {string}")
	public void user_selects_country_code(String code) {
	    // Skipped intentionally
	    System.out.println("Skipping country code selection step");
	}

	@When("user enters mobile number {string}")
	public void user_enters_mobile(String mobile) {
		signupPage.enterMobileNo(mobile);
	}

	@When("user selects option {string}")
	public void user_selects_option(String option) {
		signupPage.selectOption(option);
	}

	@When("user selects nationality {string}")
	public void user_selects_nationality(String nationality) {
		signupPage.selectCitizenship(nationality);
	}

	@When("user checks receive mail checkbox")
	public void user_checks_receive_mail_checkbox() {
		signupPage.setReceiveMail(true);
	}

	@When("user accepts terms and conditions")
	public void user_accepts_terms_and_conditions() {
		signupPage.setAgreeTerms(true);
		signupPage.setConsent(true);
	}

	@When("user enters OTP {string}")
	public void user_enters_otp(String otp) {
		signupPage.enterOtp(otp);
	}

	@Then("user verifies the account")
	public void user_verifies_the_account() {
		signupPage.clickVerify();
	}
}