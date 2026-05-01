package listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import utils.ExtentManager;

public class TestListener implements ITestListener {

	public static ExtentReports extent = ExtentManager.getInstance();
	public static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

	@Override
	public void onTestStart(ITestResult result) {

		String methodName = result.getMethod().getMethodName();
		String className = result.getTestClass().getRealClass().getSimpleName();

		String formattedName = formatTestName(methodName);

		ExtentTest extentTest = extent.createTest(formattedName);
		extentTest.assignCategory(className);
		extentTest.info("Test Class: " + className);

		test.set(extentTest);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		if (test.get() != null) {
			test.get().pass("Test Passed");
		}
	}

	@Override
	public void onTestFailure(ITestResult result) {
		if (test.get() != null) {

			String errorMessage = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown Error";

			test.get().fail("Test Failed");
			test.get().fail("Reason: " + errorMessage);
			test.get().fail(result.getThrowable());
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		if (test.get() != null) {
			test.get().skip("Test Skipped");
		}
	}

	@Override
	public void onFinish(ITestContext context) {
		extent.flush();
	}

	private String formatTestName(String methodName) {

		String name = methodName
				// Split camelCase
				.replaceAll("([a-z])([A-Z])", "$1 $2")
				// Split uppercase sequences like CRNAnd → CRN And
				.replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2")
				// Split letters and numbers
				.replaceAll("([a-zA-Z])([0-9])", "$1 $2").replaceAll("([0-9])([a-zA-Z])", "$1 $2");

		String[] words = name.split(" ");
		StringBuilder formatted = new StringBuilder();

		for (String word : words) {

			if (word.isEmpty())
				continue;

			// Keep numbers as is
			if (word.matches("\\d+")) {
				formatted.append(word);
			}
			// Abbreviations (all caps OR short words like API, OTP, CRN)
			else if (word.equals(word.toUpperCase()) && word.length() > 1) {
				formatted.append(word);
			}
			// Normal words
			else {
				formatted.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
			}

			formatted.append(" ");
		}

		return formatted.toString().trim();
	}
}