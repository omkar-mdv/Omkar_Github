package DemoQA;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

public class RobotTest {

	public static void main(String[] args) throws InterruptedException, AWTException {
		 WebDriver driver = new ChromeDriver();
	        driver.get("https://login.salesforce.com");
	        WebElement username = driver.findElement(By.id("username"));
	        username.sendKeys("omkarmadav");
	        
	        // Create Robot instance
	        Robot robot = new Robot();
	        // Simulate Ctrl+A (Select All)
	        robot.keyPress(KeyEvent.VK_CONTROL);
	        robot.keyPress(KeyEvent.VK_A);
	        robot.keyRelease(KeyEvent.VK_A);
	        robot.keyRelease(KeyEvent.VK_CONTROL);
	        Thread.sleep(1000); // Wait for a second
	        
	        // Simulate Ctrl+C (Copy)
	        robot.keyPress(KeyEvent.VK_CONTROL);
	        robot.keyPress(KeyEvent.VK_C);
	        robot.keyRelease(KeyEvent.VK_C);
	        robot.keyRelease(KeyEvent.VK_CONTROL);
	        
	        driver.navigate().to("https://www.google.com");
	        WebElement searchBox = driver.findElement(By.id("APjFqb"));
	        searchBox.click();
	        
	        // Simulate Ctrl+V (Paste)
	        robot.keyPress(KeyEvent.VK_CONTROL);
	        robot.keyPress(KeyEvent.VK_V);
	        robot.keyRelease(KeyEvent.VK_V);
	        robot.keyRelease(KeyEvent.VK_CONTROL);
	        
	        //Enter
	        robot.keyPress(KeyEvent.VK_ENTER);
	        robot.keyRelease(KeyEvent.VK_ENTER);
	        
	        Thread.sleep(3000); // Wait for 3 seconds to see the result
	        driver.quit();

	}
}
