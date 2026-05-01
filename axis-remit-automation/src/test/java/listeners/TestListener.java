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

		// Step 1: Split camel case (handles CRNAnd, APIResponse, etc.)
		String[] words = methodName.replaceAll("([a-z])([A-Z])", "$1 $2").replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2")
				.split(" ");

		StringBuilder formatted = new StringBuilder();

		for (String word : words) {

			// Step 2: If already uppercase OR short word → treat as abbreviation
			if (word.equals(word.toUpperCase()) || word.length() <= 3) {
				formatted.append(word.toUpperCase());
			} else {
				// Normal word → capitalize first letter
				formatted.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
			}

			formatted.append(" ");
		}

		return formatted.toString().trim();
	}
}