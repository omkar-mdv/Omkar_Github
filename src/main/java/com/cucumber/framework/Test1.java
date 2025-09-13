package com.cucumber.framework;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Test1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		WebDriver driver = new ChromeDriver();
		driver.get("https://demoqa.com/links");
		driver.manage().window().maximize();
		
		List<WebElement> links = driver.findElements(By.tagName("a"));
		
		System.out.println("Total links found: " + links.size());

        for (WebElement link : links) {
            String url = link.getAttribute("href");

            if (url == null || url.isEmpty()) {
                System.out.println("⚠️ Skipped: Empty or null href");
                continue;
            }

            try {
                // Open connection
                HttpURLConnection connection = (HttpURLConnection) (new URL(url).openConnection());
                connection.setRequestMethod("HEAD"); // faster than GET
                connection.connect();

                int statusCode = connection.getResponseCode();

                if (statusCode >= 400) {
                    System.out.println("❌ Broken link: " + url + " --> Status: " + statusCode);
                } else {
                    System.out.println("✅ Valid link: " + url + " --> Status: " + statusCode);
                }

            } catch (Exception e) {
                System.out.println("⚠️ Exception for: " + url + " --> " + e.getMessage());
            }
        }

        driver.quit();
		
		


	}

}
