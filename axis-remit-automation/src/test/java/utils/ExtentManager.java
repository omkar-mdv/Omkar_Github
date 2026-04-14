package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports getInstance() {

        if (extent == null) {

            String reportPath = System.getProperty("user.dir") + "/reports/APIReport.html";

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setReportName("API Automation Execution Report");
            sparkReporter.config().setDocumentTitle("Extent Report - API Testing");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            extent.setSystemInfo("Tester", "Omkar Madav");
            extent.setSystemInfo("Framework", "RestAssured + TestNG");
            extent.setSystemInfo("OS", System.getProperty("os.name"));
        }

        return extent;
    }
}