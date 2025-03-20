package com.qa.opencart.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExtentReportListener implements ITestListener {
    private static final String OUTPUT_FOLDER = "./reports/";
    private static final String FILE_NAME = "TestExecutionReport.html";
    
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    
    @Override
    public void onStart(ITestContext context) {
        System.out.println("*** Test Suite " + context.getName() + " started ***");
        initExtentReport();
    }
    
    @Override
    public void onFinish(ITestContext context) {
        System.out.println("*** Test Suite " + context.getName() + " ending ***");
        if (extent != null) {
            extent.flush();
            System.out.println("*** Extent Report flushed to: " + OUTPUT_FOLDER + FILE_NAME + " ***");
        }
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("*** Running test method " + result.getMethod().getMethodName() + "...");
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName(),
                result.getMethod().getDescription());
        test.set(extentTest);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("*** Executed " + result.getMethod().getMethodName() + " test successfully...");
        test.get().log(Status.PASS, "Test passed");
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("*** Test execution " + result.getMethod().getMethodName() + " failed...");
        test.get().log(Status.FAIL, "Test Failed: " + result.getThrowable());
        
        // Add screenshot for failed tests
        String screenshotPath = takeScreenshot(result.getMethod().getMethodName());
        if (screenshotPath != null) {
            test.get().addScreenCaptureFromPath(screenshotPath, "Screenshot on Failure");
        }
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("*** Test " + result.getMethod().getMethodName() + " skipped...");
        test.get().log(Status.SKIP, "Test Skipped: " + result.getThrowable());
    }
    
    private void initExtentReport() {
        try {
            // Create reports directory if it doesn't exist
            Path reportsPath = Paths.get(OUTPUT_FOLDER);
            if (!Files.exists(reportsPath)) {
                Files.createDirectories(reportsPath);
                System.out.println("Created reports directory: " + reportsPath.toAbsolutePath());
            }
            
            // Create screenshots directory if it doesn't exist
            Path screenshotsPath = Paths.get(OUTPUT_FOLDER + "screenshots");
            if (!Files.exists(screenshotsPath)) {
                Files.createDirectories(screenshotsPath);
                System.out.println("Created screenshots directory: " + screenshotsPath.toAbsolutePath());
            }
            
            ExtentSparkReporter htmlReporter = new ExtentSparkReporter(OUTPUT_FOLDER + FILE_NAME);
            htmlReporter.config().setDocumentTitle("Automation Test Results");
            htmlReporter.config().setReportName("Test Results");
            
            extent = new ExtentReports();
            extent.attachReporter(htmlReporter);
            extent.setSystemInfo("Automation Tester", "QA Team");
            extent.setSystemInfo("Organization", "OpenCart");
            extent.setSystemInfo("Build No", "1.0");
            
            System.out.println("Extent Report initialized at: " + OUTPUT_FOLDER + FILE_NAME);
        } catch (Exception e) {
            System.err.println("Failed to initialize Extent Report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String takeScreenshot(String testName) {
        try {
            Path screenshotsPath = Paths.get(OUTPUT_FOLDER + "screenshots");
            if (!Files.exists(screenshotsPath)) {
                Files.createDirectories(screenshotsPath);
            }
            
            String screenshotFileName = testName + "_" + System.currentTimeMillis() + ".png";
            String screenshotPath = OUTPUT_FOLDER + "screenshots/" + screenshotFileName;
            
            // Note: In a real implementation, you would capture the screenshot here
            // For demonstration, we're just creating an empty file
            Files.createFile(Paths.get(screenshotPath));
            
            return screenshotPath;
        } catch (IOException e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
} 