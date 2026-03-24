package stepdefinitions;

import java.time.Duration;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import pages.LoginPage;
import io.cucumber.java.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;

public class StepDef {

	public WebDriver driver;
	public LoginPage lp;

	
	@Given("user launched the browser")
	public void user_launched_the_browser() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		lp = new LoginPage(driver);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
	}

	@When("user opens the URL {string}")
	public void user_opens_the_url(String url) {
		driver.get(url);
	}

	@When("user enters email {string} and password {string}")
	public void user_enters_email_and_password(String email, String pass) {
		lp.setEmail(email);
		lp.setPassword(pass);
	}

	@When("clicks on the login button")
	public void clicks_on_the_login_button() {
		lp.clickLogin();
	}

	@Then("the page title should be {string}")
	public void the_page_title_should_be(String expectedTitle) {
		String actualTitle = driver.getTitle();
		if (actualTitle.equals(expectedTitle)) {
			Assert.assertTrue(true);
		} else {
			Assert.assertTrue(false);
		}
	}

	@When("user clicks on the logout button")
	public void user_clicks_on_the_logout_button() {
		lp.clickLogout();
	}

	@Then("page title should be {string}")
	public void page_title_should_be(String expectedTitle) {
		String actualTitle = driver.getTitle();
		if (actualTitle.equals(expectedTitle)) {
			Assert.assertTrue(true);
		} else {
			Assert.assertTrue(false);
		}
	}

	@Then("close the browser")
	public void close_the_browser() {
		driver.quit();
	}
	
	@Then("page title should be displayed")
	public void page_title_should_be_displayed() {
	    String title = driver.getTitle();
	    System.out.println("Page Title is: " + title);
	    Assert.assertTrue(title.length() > 0);
	}

}