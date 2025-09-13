package com.cucumber.framework;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Test2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		WebDriver driver = new ChromeDriver();
		driver.get("https://www.google.com/");
		driver.manage().window().maximize();
		
		driver.findElement(By.id("APjFqb")).click();
		
		List<WebElement> allOptions = driver.findElements(By.xpath("//ul[@role='listbox']//li"));
		System.out.println("Total suggestions: " + allOptions.size());
		
		for (WebElement suggestion : allOptions) {
			System.out.println(suggestion.getText());
			if (suggestion.getText().contains("apple")) {
				suggestion.click();
				break;
			}
		}
		
//		driver.quit();

	}

}
