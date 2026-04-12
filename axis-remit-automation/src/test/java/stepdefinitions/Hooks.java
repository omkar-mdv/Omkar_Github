package stepdefinitions;

import base.BaseTest;
import io.cucumber.java.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hooks {

	@Before
	public void setup() {
		BaseTest.initDriver();
	}

	@After
	public void tearDown(Scenario scenario) {

		// ✅ Capture screenshot only if scenario fails
		if (scenario.isFailed()) {

			byte[] screenshot = ((TakesScreenshot) BaseTest.getDriver()).getScreenshotAs(OutputType.BYTES);

			// Attach to Cucumber report
			scenario.attach(screenshot, "image/png", scenario.getName());
		}

		BaseTest.quitDriver();
	}
}