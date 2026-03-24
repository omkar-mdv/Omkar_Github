package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class EuroSignupPage {
	
	WebDriver driver;
	
	public EuroSignupPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(id = "country")
	WebElement drpCountry;
	
	@FindBy(id = "name")
	WebElement txtName;
	
	@FindBy(id = "emailId")
	WebElement txtEmail;
	
	@FindBy(id = "newPassword")
	WebElement txtPassword;
	
	@FindBy(id = "mobilePhoneCode")
	WebElement drpMobileCode;
	
	@FindBy(id = "mobileNo")
	WebElement txtMobileNo;
	
	@FindBy(xpath = "//span[text()='Indian']")
	WebElement citizenship1;
	
	@FindBy(xpath = "//span[text()='Other']")
	WebElement citizepnship2;
	
	@FindBy(id = "recvMailChk")
	WebElement receiveMailCheckbox;
	
	@FindBy(id = "terms")
	WebElement agreeTermsCheckbox;
	
	@FindBy(id = "consentChkBox")
	WebElement conscentCheckbox;
	
	@FindBy(xpath = "//button[text()='CLEAR']")
	WebElement btnClear;
	
	@FindBy(xpath = "//button[text()='REGISTER NOW']")
	WebElement btnRegister;
	
	public void selectCountry(String country) {
		drpCountry.sendKeys(country);
	}
	
	public void enterName(String name) {
		txtName.sendKeys(name);
	}
	
	public void enterEmail(String email) {
		txtEmail.sendKeys(email);
	}
	
	public void enterPassword(String password) {
		txtPassword.sendKeys(password);
	}
	
	public void selectMobileCode(String code) {
		drpMobileCode.sendKeys(code);
	}
	
	public void enterMobileNo(String mobileNo) {
		txtMobileNo.sendKeys(mobileNo);
	}
	
	public void selectCitizenship(String citizenship) {
		if(citizenship.equalsIgnoreCase("Indian")) {
			citizenship1.click();
		} else if(citizenship.equalsIgnoreCase("Other")) {
			citizepnship2.click();
		}
	}
	
	public void checkReceiveMail(boolean receive) {
		if(receive) {
			if(!receiveMailCheckbox.isSelected()) {
				receiveMailCheckbox.click();
			}
		} else {
			if(receiveMailCheckbox.isSelected()) {
				receiveMailCheckbox.click();
			}
		}
	}
	
	public void checkAgreeTerms(boolean agree) {
		if(agree) {
			if(!agreeTermsCheckbox.isSelected()) {
				agreeTermsCheckbox.click();
			}
		} else {
			if(agreeTermsCheckbox.isSelected()) {
				agreeTermsCheckbox.click();
			}
		}
	}
	
	public void checkConsent(boolean consent) {
		if(consent) {
			if(!conscentCheckbox.isSelected()) {
				conscentCheckbox.click();
			}
		} else {
			if(conscentCheckbox.isSelected()) {
				conscentCheckbox.click();
			}
		}
	}
	
	public void clickClear() {
		btnClear.click();
	}
	
	public void clickRegister() {
		btnRegister.click();
	}
	
}

