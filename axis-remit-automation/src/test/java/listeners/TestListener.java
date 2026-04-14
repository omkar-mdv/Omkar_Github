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

        // Show class name in report
        extentTest.assignCategory(className);

        // Optional extra info
        extentTest.info("Test Class: " + className);

        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().pass("Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().fail(result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    private String formatTestName(String methodName) {

        String name = methodName.replaceAll("([a-z])([A-Z])", "$1 $2");

        name = name.replace("Api", "API");
        name = name.replace("Otp", "OTP");
        name = name.replace("Crn", "CRN");
        name = name.replace("Acc", "Account");

        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}