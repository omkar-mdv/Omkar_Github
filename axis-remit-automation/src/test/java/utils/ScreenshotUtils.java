package utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotUtils {

	public static String captureScreenshot(WebDriver driver, String scenarioName) {

		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String fileName = scenarioName + "_" + timestamp + ".png";

		String path = System.getProperty("user.dir") + "/screenshots/" + fileName;

		TakesScreenshot ts = (TakesScreenshot) driver;
		File src = ts.getScreenshotAs(OutputType.FILE);

		try {
			FileUtils.copyFile(src, new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return path;
	}
}