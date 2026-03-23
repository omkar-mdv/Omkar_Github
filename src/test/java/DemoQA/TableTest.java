package DemoQA;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class TableTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TableTest test = new TableTest();
		test.testTable();
	}
	
	public void testTable() {
		// Implementation for table testing goes here
		WebDriver driver = new ChromeDriver();
		driver.get("https://www.facebook.com/");

		driver.navigate().back();
		driver.navigate().forward();
		driver.navigate().refresh();
		driver.navigate().to("https://www.google.com/");
		
		driver.quit();
	}
}
