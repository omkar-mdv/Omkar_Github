package TestRunner;

import org.junit.runner.RunWith;

import io.cucumber.junit.*;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "./Features/Login.feature",
		glue = "StepDefinition",
		dryRun = false,
		monochrome = true,
		tags = "@smoke",
		plugin = {"pretty", "html:target/HtmlReports/report.html"}
		)

public class Runner {
	// This is empty on purpose
}
