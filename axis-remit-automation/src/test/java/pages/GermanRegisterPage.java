package pages;

import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

public class GermanRegisterPage {

	WebDriver driver;
	WebDriverWait wait;

	public GermanRegisterPage(WebDriver driver) {

		this.driver = driver;
		PageFactory.initElements(driver, this);
		wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	// ================= WEB ELEMENTS ================= //

	@FindBy(xpath = "//select[@id='accountType']")
	WebElement drpBlockAccountType;

	@FindBy(xpath = "//input[@id='panNumber0']")
	WebElement txtPanNumber;

	@FindBy(xpath = "//input[@id='otpCode1']")
	WebElement txtPanOtp;

	@FindBy(xpath = "(//button[@id='otpCodeVerify'])[1]")
	WebElement btnPanVerify;

	@FindBy(xpath = "//input[@id='emailId0']")
	WebElement txtEmail;

	@FindBy(xpath = "//button[@id='getOtp1']")
	WebElement btnGetOtp;

	@FindBy(xpath = "//input[@id='otpCode']")
	WebElement txtEmailOtp;

	@FindBy(xpath = "(//button[@id='otpCodeVerify'])[2]")
	WebElement btnEmailVerify;

	@FindBy(xpath = "//input[@id='password0']")
	WebElement txtPassword;

	@FindBy(xpath = "//input[@id='confirmpassword0']")
	WebElement txtConfirmPassword;

	@FindBy(xpath = "//input[@id='dob0']")
	WebElement txtDob;

	@FindBy(xpath = "//input[@id='address0']")
	WebElement txtAddress;

	@FindBy(xpath = "//input[@id='passportNumber0']")
	WebElement txtPassportNumber;

	@FindBy(xpath = "//input[@id='expiryDate0']")
	WebElement txtPassportExpiry;

	@FindBy(xpath = "//select[@id='state0']")
	WebElement drpState;

	@FindBy(xpath = "//select[@id='city0']")
	WebElement drpCity;

	@FindBy(xpath = "//input[@id='pincode0']")
	WebElement txtPincode;

	@FindBy(xpath = "//select[@id='multiAccNo']")
	WebElement drpAccountNumber;

	@FindBy(xpath = "//input[@id='firstName1']")
	WebElement txtFirstName;

	@FindBy(xpath = "//input[@id='lastName1']")
	WebElement txtLastName;

	@FindBy(xpath = "//input[@id='middleName1']")
	WebElement txtMiddleName;

	@FindBy(xpath = "//input[@id='mobile1']")
	WebElement txtMobileNumber;

	@FindBy(xpath = "//input[@id='emailId1']")
	WebElement txtEmergencyEmail;

	@FindBy(xpath = "//input[@id='TC']")
	WebElement chkTerms;

	@FindBy(xpath = "//button[normalize-space()='I Accept']")
	WebElement btnIAccept;

	@FindBy(xpath = "(//button[normalize-space()='Next'])[1]")
	WebElement btnNext;

	@FindBy(xpath = "//a[normalize-space()='Click here']")
	WebElement btnClickHere;

	@FindBy(xpath = "//input[@id='a2ContBtn']")
	WebElement btnContinue;

	@FindBy(xpath = "//button[@id='Loginbutton']")
	WebElement btnFinalNext;

	@FindBy(xpath = "//h3[normalize-space()='Your registration was successful!']")
	WebElement successMessage;

	@FindBy(xpath = "//span[contains(text(),'Final Rate is subject to change at the time of rem')]")
	WebElement txtOutsideClick;

	// ================= COMMON METHODS ================= //

	public void safeClick(WebElement element) {

		wait.until(ExpectedConditions.visibilityOf(element));

		Actions actions = new Actions(driver);
		actions.moveToElement(element).pause(Duration.ofMillis(300)).click().perform();
	}

	// ================= ACTION METHODS ================= //

	public void openGermanRegistrationPage(String url) {

		driver.get(url);
	}

	public void clickBlockAccountTypeDropdown() {

		safeClick(drpBlockAccountType);
	}

	public void selectBlockAccountType(String accountType) {

		Select select = new Select(drpBlockAccountType);
		select.selectByVisibleText(accountType);
	}

	public void enterPanNumber(String panNo) {

		txtPanNumber.sendKeys(panNo);
	}

	public void enterPanOtp(String otp) {

		txtPanOtp.sendKeys(otp);
	}

	public void clickPanVerifyButton() {

		safeClick(btnPanVerify);
	}

	public void enterEmail(String email) {

		txtEmail.sendKeys(email);
	}

	public void clickGetOtpButton() {

		safeClick(btnGetOtp);
	}

	public void enterEmailOtp(String otp) {

		txtEmailOtp.sendKeys(otp);
	}

	public void clickEmailVerifyButton() {

		safeClick(btnEmailVerify);
	}

	public void enterPassword(String password) {

		txtPassword.sendKeys(password);
	}

	public void enterConfirmPassword(String password) {

		txtConfirmPassword.sendKeys(password);
	}

	public void enterDob(String dob) {

		txtDob.sendKeys(dob);
	}

	public void enterAddress(String address) {

		txtAddress.sendKeys(address);
	}

	public void enterPassportNumber(String passportNo) {

		txtPassportNumber.sendKeys(passportNo);
	}

	public void enterPassportExpiry(String expiryDate) {

		txtPassportExpiry.sendKeys(expiryDate);
	}

	public void selectState(String state) {

		Select select = new Select(drpState);
		select.selectByVisibleText(state);
	}

	public void selectCity(String city) {

		Select select = new Select(drpCity);
		select.selectByVisibleText(city);
	}

	public void enterPincode(String pincode) {

		txtPincode.sendKeys(pincode);
	}

	public void selectAccountNumber(String accountNo) {

		Select select = new Select(drpAccountNumber);
		select.selectByVisibleText(accountNo);
	}

	public void enterFirstName(String firstName) {

		txtFirstName.sendKeys(firstName);
	}

	public void enterLastName(String lastName) {

		txtLastName.sendKeys(lastName);
	}

	public void enterMiddleName(String middleName) {

		txtMiddleName.sendKeys(middleName);
	}

	public void enterMobileNumber(String mobileNo) {

		txtMobileNumber.sendKeys(mobileNo);
	}

	public void enterEmergencyEmail(String email) {

		txtEmergencyEmail.sendKeys(email);
	}

	public void clickTermsCheckbox() {

		safeClick(chkTerms);
	}

	public void clickIAcceptButton() {

		safeClick(btnIAccept);
	}

	public void clickNextButton() {

		safeClick(btnNext);
	}

	public void clickClickHereButton() {

		safeClick(btnClickHere);
	}

	public void clickContinueButton() {

		safeClick(btnContinue);
	}

	public void clickFinalNextButton() {

		safeClick(btnFinalNext);
	}

	public void acceptPopup() {

		Alert alert = wait.until(ExpectedConditions.alertIsPresent());
		alert.accept();
	}

	public String getSuccessMessage() {

		wait.until(ExpectedConditions.visibilityOf(successMessage));

		return successMessage.getText();
	}

	public void clickOutsideAfterPan() {
		safeClick(txtOutsideClick);
	}
}