package pages;

import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.*;

public class EuroLoginPage {

	WebDriver driver;
	WebDriverWait wait;

	public EuroLoginPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	// ================= LOCATORS ================= //

	@FindBy(id = "loginId")
	WebElement txtEmail;

	@FindBy(id = "loginpassword")
	WebElement txtPassword;

	@FindBy(xpath = "//button[normalize-space()='LOG IN']")
	WebElement btnLogin;

	// ================= ACTION METHODS ================= //

	public void safeClick(WebElement element) {

		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(element.getAttribute("id"))));

		Actions actions = new Actions(driver);
		actions.moveToElement(element).pause(Duration.ofMillis(200)).click().perform();
	}

	public void enterEmail(String email) {
		wait.until(ExpectedConditions.visibilityOf(txtEmail)).sendKeys(email);
	}

	public void enterPassword(String password) {
		wait.until(ExpectedConditions.visibilityOf(txtPassword)).sendKeys(password);
	}

	public void clickLogin() {

		wait.until(ExpectedConditions.elementToBeClickable(btnLogin));
		btnLogin.click();
	}
}