package DemoQA;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class JSExecute {

	public static void main(String[] args) {
		JSExecute test = new JSExecute();
		test.testJSExecute();
	}
	
	public void testJSExecute() {
	
		WebDriver driver = new ChromeDriver();
		driver.get("https://demoqa.com/");
		
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("alert('Hello from JavaScript Executor');");
		
		driver.switchTo().parentFrame();
		driver.switchTo().defaultContent();
		driver.quit();
		
	}

}
