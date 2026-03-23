package DemoQA;

import java.awt.AWTException;
import java.awt.Robot;
import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AlertsTest {

	public static void main(String[] args) throws AWTException {
		AlertsTest test = new AlertsTest();
		test.testAlerts();
	}
	
	public void testAlerts() throws AWTException {
		Alert alert;
		WebDriverWait wait;
		WebDriver driver = new ChromeDriver();
		Actions action = new Actions(driver);
		driver.get("https://demoqa.com/alerts");
		driver.manage().window().maximize();
		
		
//		driver.switchTo().newWindow(WindowType.TAB);
		Robot r = new Robot();
		
		
		
		WebElement button1 = driver.findElement(By.id("alertButton"));
		System.out.println(button1.getAttribute("id"));
//		button1.click();
//		Alert alert = driver.switchTo().alert();
//		System.out.println(alert.getText());
//		alert.accept();

//		WebElement button3 = driver.findElement(By.id("confirmButton"));
//		button3.click();
		
		
		driver.quit();
		
	}

}
