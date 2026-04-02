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
    
    @FindBy(xpath = "//div[contains(@class,'ant-notification-notice-message')]")
    private WebElement successMessage;

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

        WebElement element = driver.findElement(
            By.xpath("//span[normalize-space()='" + option + "']")
        );

        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].click();", element
        );
    }

    public void selectCitizenship(String citizenship) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Target LABEL instead of SPAN
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//label[.//span[normalize-space()='" + citizenship + "']]")
        ));

        // Scroll to center (fix viewport issue)
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // JS click (avoids interception)
        js.executeScript("arguments[0].click();", element);
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

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement verifyBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[normalize-space()='VERIFY ACCOUNT']")
        ));

        // Scroll to center (CRITICAL FIX)
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", verifyBtn);

        // wait for UI stability
        try { Thread.sleep(700); } catch (InterruptedException e) {}

        // Wait until clickable
        wait.until(ExpectedConditions.elementToBeClickable(verifyBtn));

        // JS click (final reliable click)
        js.executeScript("arguments[0].click();", verifyBtn);
    }
    
    public String getSuccessMessage() {
        return wait.until(ExpectedConditions.visibilityOf(successMessage)).getText();
    }
}