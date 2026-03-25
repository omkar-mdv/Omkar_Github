package pages;

import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

public class EuroSignupPage {

    WebDriver driver;
    WebDriverWait wait;

    public EuroSignupPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @FindBy(id = "country")
    WebElement drpCountry;

    @FindBy(id = "euro_country")
    WebElement drpEuroCountry;

    @FindBy(id = "name")
    WebElement txtName;

    @FindBy(id = "emailId")
    WebElement txtEmail;

    @FindBy(id = "newPassword")
    WebElement txtPassword;

    @FindBy(id = "mobileNo")
    WebElement txtMobileNo;

    @FindBy(id = "recvMailChk")
    WebElement receiveMailCheckbox;

    @FindBy(id = "terms")
    WebElement agreeTermsCheckbox;

    @FindBy(id = "consentChkBox")
    WebElement consentCheckbox;

    @FindBy(xpath = "//button[normalize-space()='REGISTER NOW']")
    WebElement btnRegister;

    @FindBy(xpath = "//a[normalize-space()='SIGN UP']")
    WebElement btnSignup;

    @FindBy(id = "otp")
    WebElement txtOtp;

    @FindBy(xpath = "//button[normalize-space()='VERIFY ACCOUNT']")
    WebElement btnVerify;

    // ================= ACTION METHODS ================= //

    public void clickSignup() {
        WebElement signupBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@href='#/signup']")
        ));

        signupBtn.click();
    }

    public void selectCountry(String country) {
        drpCountry.click();
        WebElement option = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//span[contains(text(),'" + country + "')]")));
        option.click();
    }

    public void selectEuroCountry(String euroCountry) {
        drpEuroCountry.click();
        WebElement option = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//div[normalize-space()='" + euroCountry + "']")));
        option.click();
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
        WebElement dropdown = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//span[@title='" + code + "']")));
        dropdown.click();

        WebElement option = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath("//div[contains(text(),'" + code + "')]")));
        option.click();
    }

    public void enterMobileNo(String mobileNo) {
        txtMobileNo.sendKeys(mobileNo);
    }

    public void selectOption(String option) {
        driver.findElement(By.xpath("//span[normalize-space()='" + option + "']")).click();
    }

    public void selectCitizenship(String citizenship) {
        WebElement element = driver.findElement(
                By.xpath("//span[normalize-space()='" + citizenship + "']"));
        element.click();
    }

    public void setReceiveMail(boolean value) {
        if (receiveMailCheckbox.isSelected() != value) {
            receiveMailCheckbox.click();
        }
    }

    public void setAgreeTerms(boolean value) {
        if (agreeTermsCheckbox.isSelected() != value) {
            agreeTermsCheckbox.click();
        }
    }

    public void setConsent(boolean value) {
        if (consentCheckbox.isSelected() != value) {
            consentCheckbox.click();
        }
    }

    public void clickRegister() {
        wait.until(ExpectedConditions.elementToBeClickable(btnRegister)).click();
    }

    public void enterOtp(String otp) {
        wait.until(ExpectedConditions.visibilityOf(txtOtp)).sendKeys(otp);
    }

    public void clickVerify() {
        wait.until(ExpectedConditions.elementToBeClickable(btnVerify)).click();
    }
}