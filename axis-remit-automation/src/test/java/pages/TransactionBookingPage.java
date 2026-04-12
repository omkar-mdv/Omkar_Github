package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TransactionBookingPage {

	WebDriver driver;
	WebDriverWait wait;

	public TransactionBookingPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath = "//a[text()='SEND MONEY']")
	WebElement sendMoneyButton;

	@FindBy(xpath = "//button[text()='ADD NEW RECEIVER']")
	WebElement addNewReceiverButton;

	@FindBy(xpath = "//div[text()='Receiver Details']")
	WebElement receiverDetailsHeader;

	@FindBy(xpath = "//input[@value='Individual']")
	WebElement receiverTypeDropdown;

	@FindBy(id = "fullName")
	WebElement receiverNameInput;

	@FindBy(xpath = "//p[text()='AXIS']")
	WebElement receiverBankDropdown;

	@FindBy(id = "accountNo")
	WebElement receiverAccountNumberInput;

	@FindBy(id = "address1")
	WebElement receiverAddressInput;

	@FindBy(id = "state")
	WebElement stateDropdown;

	@FindBy(id = "city")
	WebElement cityInput;

	@FindBy(id = "zipcode")
	WebElement pincodeInput;
	
	@FindBy(id = "mobileNo")
	WebElement mobileInput;

	@FindBy(id = "emailIds")
	WebElement receiverEmailInput;

	@FindBy(id = "relationship")
	WebElement relationDropdown;

	@FindBy(xpath = "//img[@src='/static/media/familyNew.6cc2ebcd5117573b527b.jpg']")
	WebElement purposeDropdown;

	@FindBy(id = "paymentAlert")
	WebElement sendPaymentAlertCheckbox;

	@FindBy(id = "message")
	WebElement messageInput;

	@FindBy(xpath = "//button[text()='Next Step']")
	WebElement nextStepButton;

	/** Clicks on Send Money button */
	public void clickSendMoney() {
		wait.until(ExpectedConditions.elementToBeClickable(sendMoneyButton)).click();
	}

	/** Clicks on Add New Receiver button */
	public void clickAddNewReceiver() {
		wait.until(ExpectedConditions.elementToBeClickable(addNewReceiverButton)).click();
	}

	/** Verifies Receiver Details page is displayed */
	public boolean isReceiverDetailsPageDisplayed() {
		return wait.until(ExpectedConditions.visibilityOf(receiverDetailsHeader)).isDisplayed();
	}

	/** Selects receiver type */
	public void selectReceiverType(String type) {
		receiverTypeDropdown.click();
		selectFromDropdown(type);
	}

	/** Enters receiver name */
	public void enterReceiverName(String name) {
		receiverNameInput.clear();
		receiverNameInput.sendKeys(name);
	}

	/** Selects receiver bank */
	public void selectReceiverBank(String bankName) {
		receiverBankDropdown.click();
		selectFromDropdown(bankName);
	}

	/** Enters receiver account number */
	public void enterAccountNumber(String accountNumber) {
		receiverAccountNumberInput.sendKeys(accountNumber);
	}

	/** Enters receiver address */
	public void enterAddress(String address) {
		receiverAddressInput.sendKeys(address);
	}

	/** Selects state */
	public void selectState(String state) {
		stateDropdown.click();
		selectFromDropdown(state);
	}

	/** Enters city */
	public void enterCity(String city) {
		cityInput.sendKeys(city);
	}

	/** Enters pincode */
	public void enterPincode(String pincode) {
		pincodeInput.sendKeys(pincode);
	}

	/** Enters receiver email */
	public void enterReceiverEmail(String email) {
		receiverEmailInput.sendKeys(email);
	}

	/** Selects relation with receiver */
	public void selectRelation(String relation) {
		relationDropdown.click();
		selectFromDropdown(relation);
	}

	/** Selects purpose of sending money */
	public void selectPurpose(String purpose) {
		purposeDropdown.click();
		selectFromDropdown(purpose);
	}

	/** Enables Send Payment Alert checkbox */
	public void enableSendPaymentAlert() {
		if (!sendPaymentAlertCheckbox.isSelected()) {
			sendPaymentAlertCheckbox.click();
		}
	}

	/** Enters message */
	public void enterMessage(String message) {
		messageInput.sendKeys(message);
	}

	/** Clicks on Next Step button */
	public void clickNextStep() {
		wait.until(ExpectedConditions.elementToBeClickable(nextStepButton)).click();
	}

	/** Selects value from Ant Design dropdown list */
	public void selectFromDropdown(String text) {
		String xpath = "//div[contains(@class,'ant-select-item') and normalize-space()='" + text + "']";
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
	}
}