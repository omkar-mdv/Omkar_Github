package DemoQA;

import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class WindowHandle {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WindowHandle test = new WindowHandle();
		test.testWindowHandle();
	}
	
	public void testWindowHandle() {
		// Implementation for window handle testing goes here
		WebDriver driver = new ChromeDriver();
		driver.get("https://demoqa.com/browser-windows/");
		driver.manage().window().maximize();
		
		WebElement newWindow = driver.findElement(By.id("windowButton"));
		newWindow.click();
		
		String parentWIndow = driver.getWindowHandle();
		Set<String> allWindows = driver.getWindowHandles();
		
		for(String newWindowHandle : allWindows) {
			if(!newWindow.equals(parentWIndow)) {
				driver.switchTo().window(newWindowHandle);
				driver.close();
				driver.switchTo().window(parentWIndow);
				driver.quit();
			}
		}
	}

}
