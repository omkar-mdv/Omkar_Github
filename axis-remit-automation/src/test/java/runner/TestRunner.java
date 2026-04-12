package runner;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

//@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",   // path to feature files
    glue = {"stepdefinitions"},                  // step definitions package
    dryRun = false,
    	monochrome = true,
    plugin = {"pretty","html:target/cucumber-reports.html",
    		"json:target/cucumber.json",
    	    "junit:target/cucumber.xml"
    }
)

public class TestRunner extends AbstractTestNGCucumberTests {
	// This class serves as the test runner for Cucumber tests using TestNG.
}