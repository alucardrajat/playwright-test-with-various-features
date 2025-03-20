package com.qa.opencart.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExtentReportTest {
    private static final String REPORT_PATH = "./direct-reports/TestReport.html";
    private ExtentReports extent;
    
    @BeforeClass
    public void setup() {
        try {
            // Create directory if it doesn't exist
            Path reportDir = Paths.get("./direct-reports");
            if (!Files.exists(reportDir)) {
                Files.createDirectories(reportDir);
                System.out.println("Created report directory: " + reportDir.toAbsolutePath());
            }
            
            // Initialize ExtentReports
            ExtentSparkReporter htmlReporter = new ExtentSparkReporter(REPORT_PATH);
            htmlReporter.config().setDocumentTitle("Direct Test Report");
            htmlReporter.config().setReportName("Test Results");
            
            extent = new ExtentReports();
            extent.attachReporter(htmlReporter);
            extent.setSystemInfo("Tester", "QA Team");
            
            System.out.println("Extent Report initialized at: " + REPORT_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testDirectReport() {
        ExtentTest test = extent.createTest("Direct Report Test", "Test to verify direct report generation");
        test.log(Status.INFO, "Starting test");
        
        // Perform some test actions
        System.out.println("Running direct report test");
        
        // Log test steps
        test.log(Status.PASS, "Test step 1 passed");
        test.log(Status.PASS, "Test step 2 passed");
        
        // Assert something
        Assert.assertTrue(true, "This test should always pass");
        
        test.log(Status.PASS, "Test completed successfully");
    }
    
    @AfterClass
    public void tearDown() {
        if (extent != null) {
            extent.flush();
            System.out.println("Extent Report flushed to: " + REPORT_PATH);
            System.out.println("Please check the report at: " + new File(REPORT_PATH).getAbsolutePath());
        }
    }
} 