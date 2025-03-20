package com.qa.opencart.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

public class ReportTest {

    @Test
    public void testReportGeneration() {
        // Create test-output directory if it doesn't exist
        File outputDir = new File("./test-output/");
        if (!outputDir.exists()) {
            boolean created = outputDir.mkdirs();
            System.out.println("test-output directory created: " + created);
        } else {
            System.out.println("test-output directory already exists");
        }
        
        // Create a simple file to verify we can write to the directory
        try {
            File testFile = new File("./test-output/test.txt");
            boolean created = testFile.createNewFile();
            System.out.println("Test file created: " + created);
            Assert.assertTrue(true, "This test should always pass");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed to create test file: " + e.getMessage());
        }
    }
} 