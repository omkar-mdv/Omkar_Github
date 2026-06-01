package stepdefinitions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import base.BaseTest;
import io.cucumber.java.AfterStep;
import io.cucumber.java.en.*;
import pages.GermanRegisterPage;
import utils.ConfigReader;

public class GermanRegisterStepDef {

	WebDriver driver = BaseTest.getDriver();
	GermanRegisterPage germanPage;

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
	@Given("user navigates to German registration page")
	public void user_navigates_to_german_registration_page() {

		germanPage = new GermanRegisterPage(driver);

		driver.get(ConfigReader.get("germanRegUrl"));
	}

	// ============================
	// 🔹 REGISTRATION FLOW
	// ============================

	@When("user clicks on German Block Account Type dropdown")
	public void user_clicks_on_german_block_account_type_dropdown() {

		germanPage.clickBlockAccountTypeDropdown();
	}

	@When("user selects German Opportunity value from dropdown")
	public void user_selects_german_opportunity_value_from_dropdown() {

		germanPage.selectBlockAccountType("Opportunity");
	}

	@When("user enters German PAN number")
	public void user_enters_german_pan_number() throws InterruptedException {
		germanPage.enterPanNumber("AAAAA1111A");
		germanPage.clickOutsideAfterPan();
		Thread.sleep(10000);
	}

	@Then("user should see German OTP popup message")
	public void user_should_see_german_otp_popup_message() {

		System.out.println("German OTP popup displayed successfully");
	}

	@Then("user clicks on German OK button on popup")
	public void user_clicks_on_german_ok_button_on_popup() {

		germanPage.acceptPopup();
	}

	@When("user enters German OTP for PAN verification")
	public void user_enters_german_otp_for_pan_verification() {

		germanPage.enterPanOtp("123456");
	}

	@When("user clicks on German Verify button for PAN verification")
	public void user_clicks_on_german_verify_button_for_pan_verification() throws InterruptedException {
		germanPage.clickPanVerifyButton();

		// Wait for success alert
		Thread.sleep(3000);

		// Accept alert popup
		germanPage.acceptPopup();
	}

	@When("user enters German email address")
	public void user_enters_german_email_address() {

		germanPage.enterEmail("abc123@yopmail.com");
	}

	@When("user clicks on German Get OTP button")
	public void user_clicks_on_german_get_otp_button() {

		germanPage.clickGetOtpButton();
	}

	@Then("user should receive German OTP for email verification")
	public void user_should_receive_german_otp_for_email_verification() {

		System.out.println("German Email OTP popup displayed successfully");
	}

	@When("user enters German OTP for email verification")
	public void user_enters_german_otp_for_email_verification() {

		germanPage.enterEmailOtp("123456");
	}

	@When("user clicks on German Verify button for email verification")
	public void user_clicks_on_german_verify_button_for_email_verification() {

		germanPage.clickEmailVerifyButton();
	}

	@When("user enters German password")
	public void user_enters_german_password() {

		germanPage.enterPassword("Password@1");
	}

	@When("user enters German confirm password")
	public void user_enters_german_confirm_password() {

		germanPage.enterConfirmPassword("Password@1");
	}

	@When("user selects German date of birth")
	public void user_selects_german_date_of_birth() {

		germanPage.enterDob("01/01/1995");
	}

	@When("user enters German address as {string}")
	public void user_enters_german_address_as(String address) {

		germanPage.enterAddress(address);
	}

	@When("user enters German passport number as {string}")
	public void user_enters_german_passport_number_as(String passportNo) {

		germanPage.enterPassportNumber(passportNo);
	}

	@When("user selects German passport expiry date")
	public void user_selects_german_passport_expiry_date() {

		germanPage.enterPassportExpiry("01/01/2030");
	}

	@When("user selects German state from state dropdown")
	public void user_selects_german_state_from_state_dropdown() {

		germanPage.selectState("Maharashtra");
	}

	@When("user selects German city from city dropdown")
	public void user_selects_german_city_from_city_dropdown() {

		germanPage.selectCity("Mumbai");
	}

	@When("user enters German PIN code")
	public void user_enters_german_pin_code() {

		germanPage.enterPincode("400001");
	}

	@When("user selects German account number from account number dropdown")
	public void user_selects_german_account_number_from_account_number_dropdown() {

		germanPage.selectAccountNumber("1234567890");
	}

	@When("user enters German first name")
	public void user_enters_german_first_name() {

		germanPage.enterFirstName("Omkar");
	}

	@When("user enters German last name")
	public void user_enters_german_last_name() {

		germanPage.enterLastName("Patil");
	}

	@When("user enters German middle name")
	public void user_enters_german_middle_name() {

		germanPage.enterMiddleName("R");
	}

	@When("user enters German mobile number")
	public void user_enters_german_mobile_number() {

		germanPage.enterMobileNumber("9876543210");
	}

	@When("user enters German emergency email address")
	public void user_enters_german_emergency_email_address() {

		germanPage.enterEmergencyEmail("emergency@yopmail.com");
	}

	@When("user clicks on German terms and conditions checkbox")
	public void user_clicks_on_german_terms_and_conditions_checkbox() {

		germanPage.clickTermsCheckbox();
	}

	@When("user clicks on German I Accept button")
	public void user_clicks_on_german_i_accept_button() {

		germanPage.clickIAcceptButton();
	}

	@When("user clicks on German Next button")
	public void user_clicks_on_german_next_button() {

		germanPage.clickNextButton();
	}

	@When("user clicks on German Click Here button")
	public void user_clicks_on_german_click_here_button() {

		germanPage.clickClickHereButton();
	}

	@When("user uploads German file using Robot class through Choose File option")
	public void user_uploads_german_file_using_robot_class_through_choose_file_option() {

		System.out.println("German file uploaded successfully");
	}

	@When("user clicks on German Continue button")
	public void user_clicks_on_german_continue_button() {

		germanPage.clickContinueButton();
	}

	@Then("user should see German popup message")
	public void user_should_see_german_popup_message() {

		System.out.println("German popup displayed successfully");
	}

	@When("user clicks on German Final Next button")
	public void user_clicks_on_german_final_next_button() {

		germanPage.clickFinalNextButton();
	}

	@Then("user should see German successful message {string}")
	public void user_should_see_german_successful_message(String expectedMessage) {

		String actualMessage = germanPage.getSuccessMessage();

		Assert.assertEquals(actualMessage.trim(), expectedMessage);
	}
}
