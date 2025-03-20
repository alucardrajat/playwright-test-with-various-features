package com.qa.opencart.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.microsoft.playwright.Page;
import com.qa.opencart.factory.PlaywrightFactory;
import com.qa.opencart.pages.HomePage;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class BaseTest {
    protected PlaywrightFactory pf;
    protected Page page;
    protected HomePage homePage;
    protected Properties prop;
    
    // Extent Report
    private static final String REPORT_PATH = "./reports/TestExecutionReport.html";
    protected static ExtentReports extent;
    protected static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    @BeforeSuite
    public void setupReports() {
        try {
            // Create reports directory if it doesn't exist
            File reportsDir = new File("./reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
                System.out.println("Created reports directory: " + reportsDir.getAbsolutePath());
            }
            
            // Create screenshots directory if it doesn't exist
            File screenshotsDir = new File("./reports/screenshots");
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs();
                System.out.println("Created screenshots directory: " + screenshotsDir.getAbsolutePath());
            }
            
            // Use absolute path for the report
            String absoluteReportPath = new File(REPORT_PATH).getAbsolutePath();
            
            ExtentSparkReporter htmlReporter = new ExtentSparkReporter(absoluteReportPath);
            htmlReporter.config().setDocumentTitle("OpenCart Test Report");
            htmlReporter.config().setReportName("Test Results");
            
            // Configure to enable viewing of base64 images
            htmlReporter.config().setEncoding("utf-8");
            htmlReporter.config().setJs("document.getElementsByClassName('logo')[0].style.display='none';");
            htmlReporter.config().setCss(".report-name { padding-left: 0px; } .report-name > img { float: left;height: 90%;margin-left: 30px;margin-top: 2px;width: auto; }");
            
            extent = new ExtentReports();
            extent.attachReporter(htmlReporter);
            extent.setSystemInfo("Automation Tester", "QA Team");
            extent.setSystemInfo("Organization", "OpenCart");
            extent.setSystemInfo("Build No", "1.0");
            extent.setSystemInfo("Report Path", absoluteReportPath);
            extent.setSystemInfo("Screenshots Path", screenshotsDir.getAbsolutePath());
            
            System.out.println("Extent Report initialized at: " + absoluteReportPath);
        } catch (Exception e) {
            System.err.println("Failed to initialize Extent Report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @BeforeClass
    public void setup() {
        pf = new PlaywrightFactory();
        prop = pf.init_prop();
        page = pf.initBrowser(prop);
        homePage = new HomePage(page);
    }

    @BeforeMethod
    public void setupTest(java.lang.reflect.Method method) {
        String testDescription = "";
        
        // Get test description from Test annotation if available
        if (method.isAnnotationPresent(Test.class)) {
            Test testAnnotation = method.getAnnotation(Test.class);
            testDescription = testAnnotation.description();
        }
        
        // If no description is provided, use a default one
        if (testDescription.isEmpty()) {
            testDescription = "Execution of test: " + method.getName();
        }
        
        // Create test in the report
        ExtentTest test = extent.createTest(method.getName(), testDescription);
        
        // Add test start info
        test.info("Starting test: " + method.getName());
        test.info("Test started at: " + java.time.LocalDateTime.now());
        
        extentTest.set(test);
    }

    @DataProvider(name = "deviceData")
    public Object[][] getDeviceData() {
        return new Object[][] {
            {"Macbook", "Search - Macbook"},
            {"iPhone", "Search - iPhone"},
            {"Samsung", "Search - Samsung"},
            {"iPad", "Search - iPad"}
        };
    }

    @AfterMethod
    public void tearDownTest(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        
        if (result.getStatus() == ITestResult.FAILURE) {
            // Capture screenshot for failed test
            String screenshotPath = takeScreenshot(testName + "_failure");
            extentTest.get().fail(result.getThrowable());
            
            // Add screenshot to report
            if (screenshotPath != null) {
                try {
                    // Add screenshot as base64 image
                    extentTest.get().addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
                    System.out.println("Added failure screenshot to report: " + screenshotPath);
                } catch (Exception e) {
                    extentTest.get().info("Could not attach screenshot: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            // Capture screenshot for passed test
            String screenshotPath = takeScreenshot(testName + "_success");
            extentTest.get().pass("Test passed successfully");
            
            // Add screenshot to report for passed tests too
            if (screenshotPath != null) {
                try {
                    // Add screenshot as base64 image
                    extentTest.get().addScreenCaptureFromPath(screenshotPath, "Success Screenshot");
                    System.out.println("Added success screenshot to report: " + screenshotPath);
                } catch (Exception e) {
                    extentTest.get().info("Could not attach screenshot: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else if (result.getStatus() == ITestResult.SKIP) {
            extentTest.get().skip("Test skipped: " + result.getThrowable());
        }
        
        // Add video recording link if available
        String videoPath = pf.getVideoFilePath(testName);
        if (videoPath != null) {
            // Copy video to reports directory for better accessibility
            String reportVideoPath = copyVideoToReportsDirectory(videoPath, testName);
            
            // Use the copied video path if successful, otherwise use the original
            String videoPathToUse = (reportVideoPath != null) ? reportVideoPath : videoPath;
            
            extentTest.get().info("Video recording: " + videoPathToUse);
            
            // Create a video player HTML file for better browser compatibility
            String playerHtmlPath = createVideoPlayerHtml(testName, videoPathToUse);
            
            // Add links to both direct video and player HTML
            extentTest.get().info("<a href='" + videoPathToUse + "' target='_blank'>Direct Video Link</a> | " +
                                 "<a href='" + playerHtmlPath + "' target='_blank'>Open Video Player</a>");
            
            System.out.println("Added video links to report: " + videoPathToUse);
        }
        
        // Add test environment info
        extentTest.get().info("Browser: " + prop.getProperty("browser"));
        extentTest.get().info("URL: " + prop.getProperty("url"));
        extentTest.get().info("Test completed at: " + java.time.LocalDateTime.now());
    }

    @AfterClass
    public void tearDown() {
        if (page != null) {
            page.context().browser().close();
        }
    }
    
    @AfterSuite
    public void tearDownReports() {
        if (extent != null) {
            extent.flush();
            System.out.println("Extent Report flushed to: " + REPORT_PATH);
            System.out.println("Please check the report at: " + new File(REPORT_PATH).getAbsolutePath());
            
            // Offer to start a local server for viewing reports with proper video playback
            System.out.println("\n=== VIDEO PLAYBACK SOLUTION ===");
            System.out.println("To view reports with proper video playback, you can start a local HTTP server:");
            System.out.println("1. Run the following command: mvn exec:java -Dexec.mainClass=com.qa.opencart.utils.SimpleHttpServer");
            System.out.println("2. Open http://localhost:8000/reports/TestExecutionReport.html in your browser");
            System.out.println("3. This will avoid the ERR_FILE_NOT_FOUND issues when accessing videos");
            System.out.println("==============================\n");
        }
    }
    
    private String takeScreenshot(String testName) {
        try {
            String screenshotFileName = testName + "_" + System.currentTimeMillis() + ".png";
            // Create absolute path for the screenshot
            File screenshotsDir = new File("./reports/screenshots/");
            if (!screenshotsDir.exists()) {
                screenshotsDir.mkdirs();
            }
            
            String screenshotPath = screenshotsDir.getAbsolutePath() + File.separator + screenshotFileName;
            
            homePage.takeScreenshot(screenshotPath);
            return screenshotPath;
        } catch (Exception e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Capture a screenshot and add it to the current test report
     * @param screenshotName Name for the screenshot
     * @param description Description to show in the report
     */
    protected void captureAndAttachScreenshot(String screenshotName, String description) {
        String screenshotPath = takeScreenshot(screenshotName);
        if (screenshotPath != null) {
            try {
                extentTest.get().info(description)
                          .addScreenCaptureFromPath(screenshotPath, description);
                System.out.println("Added custom screenshot to report: " + screenshotPath);
            } catch (Exception e) {
                extentTest.get().info("Could not attach screenshot: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates an HTML file with a video player for better browser compatibility
     * @param testName Name of the test
     * @param videoPath Path to the video file
     * @return Path to the HTML player file
     */
    private String createVideoPlayerHtml(String testName, String videoPath) {
        try {
            // Create player directory if it doesn't exist
            java.io.File playerDir = new java.io.File("./reports/video-players");
            if (!playerDir.exists()) {
                playerDir.mkdirs();
            }
            
            // Create player HTML file path
            String htmlFileName = testName + "_" + System.currentTimeMillis() + "_player.html";
            String htmlFilePath = "./reports/video-players/" + htmlFileName;
            
            // Get absolute paths for better browser compatibility
            File htmlFile = new File(htmlFilePath);
            File videoFile = new File(videoPath);
            
            // Calculate relative path from HTML to video
            // For simplicity, we'll also include absolute path as an option
            String relativeVideoPath = getRelativePath(htmlFile, videoFile);
            String absoluteVideoPath = videoFile.getAbsolutePath();
            
            // Create MP4 copy if possible (for browser compatibility)
            String mp4VideoPath = null;
            File mp4File = null;
            try {
                // Try to create an MP4 copy - just a simple extension rename
                if (videoPath.toLowerCase().endsWith(".webm")) {
                    String mp4FilePath = videoPath.substring(0, videoPath.lastIndexOf(".")) + ".mp4";
                    java.nio.file.Files.copy(
                        java.nio.file.Paths.get(videoPath), 
                        java.nio.file.Paths.get(mp4FilePath),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );
                    mp4File = new File(mp4FilePath);
                    mp4VideoPath = getRelativePath(htmlFile, mp4File);
                    System.out.println("Created MP4 copy at: " + mp4FilePath);
                }
            } catch (Exception e) {
                System.out.println("Note: Could not create MP4 copy. This is a simple rename and may not work for playback: " + e.getMessage());
            }
            
            // HTML content with video player
            String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Test Video: " + testName + "</title>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n" +
                "        h1 { color: #333; }\n" +
                "        .video-container { margin: 20px 0; }\n" +
                "        video { max-width: 100%; border: 1px solid #ddd; background: #000; }\n" +
                "        .notes { margin-top: 20px; padding: 15px; background: #fff; border: 1px solid #ddd; border-radius: 4px; }\n" +
                "        .download-btn { display: inline-block; padding: 10px 15px; background: #4CAF50; color: white; text-decoration: none; border-radius: 4px; margin-top: 10px; }\n" +
                "        .download-btn:hover { background: #45a049; }\n" +
                "        .tab { margin-left: 20px; }\n" +
                "        .file-paths { font-size: 12px; color: #666; }\n" +
                "        .error-message { color: red; display: none; padding: 10px; background: #ffeeee; border: 1px solid #ffcccc; margin-top: 10px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>Test Video: " + testName + "</h1>\n" +
                "    <div class=\"video-container\">\n" +
                "        <video controls>\n";
            
            // Add MP4 source first (if available) for better compatibility
            if (mp4VideoPath != null) {
                htmlContent += "            <source src=\"" + mp4VideoPath + "\" type=\"video/mp4\">\n";
            }
            
            // Add WebM source with both relative and absolute paths
            htmlContent += "            <source src=\"" + relativeVideoPath + "\" type=\"video/webm\">\n" +
                "            Your browser does not support the video tag.\n" +
                "        </video>\n" +
                "    </div>\n" +
                "    <div class=\"error-message\" id=\"errorMessage\">\n" +
                "        Video couldn't be loaded due to browser security restrictions. Try using the download button below.\n" +
                "    </div>\n" +
                "    <a href=\"" + relativeVideoPath + "\" download class=\"download-btn\">Download Video</a>\n" +
                "    <div class=\"notes\">\n" +
                "        <p><strong>Troubleshooting Video Playback:</strong></p>\n" +
                "        <ol>\n" +
                "            <li><strong>Browser Security Issues</strong> - If you're seeing ERR_FILE_NOT_FOUND, it's likely due to browser security restrictions for local files</li>\n" +
                "            <li><strong>Use a Local Server</strong> - Open these files using a local web server instead of directly from the filesystem</li>\n" +
                "            <li><strong>Try a different browser</strong> - Chrome has the best WebM support</li>\n" +
                "            <li><strong>Download and play locally</strong> - Use the Download button above</li>\n" +
                "            <li><strong>Use VLC Media Player</strong> - <a href=\"https://www.videolan.org/vlc/\" target=\"_blank\">Download VLC</a> to play WebM files</li>\n" +
                "        </ol>\n" +
                "        <p><strong>Video Information:</strong></p>\n" +
                "        <p class=\"tab\">Test Name: " + testName + "</p>\n" +
                "        <p class=\"tab\">Date: " + java.time.LocalDateTime.now() + "</p>\n" +
                "        <p class=\"tab file-paths\">Relative Path: " + relativeVideoPath + "</p>\n" +
                "        <p class=\"tab file-paths\">Absolute Path: " + absoluteVideoPath + "</p>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        // Check if video loads properly\n" +
                "        document.querySelector('video').addEventListener('error', function() {\n" +
                "            document.getElementById('errorMessage').style.display = 'block';\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
            
            // Write to file
            java.io.FileWriter writer = new java.io.FileWriter(htmlFilePath);
            writer.write(htmlContent);
            writer.close();
            
            System.out.println("Created video player HTML at: " + htmlFilePath);
            return htmlFilePath;
            
        } catch (Exception e) {
            System.err.println("Error creating video player HTML: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get the relative path from one file to another
     * @param from The file from which the relative path starts
     * @param to The file to which the relative path leads
     * @return The relative path
     */
    private String getRelativePath(File from, File to) {
        try {
            // Try to calculate a proper relative path
            String fromPath = from.getParentFile().getAbsolutePath();
            String toPath = to.getAbsolutePath();
            
            // Handle case sensitivity and normalize paths
            fromPath = fromPath.replace('\\', '/');
            toPath = toPath.replace('\\', '/');
            
            // Simple approach - could be improved for complex paths
            java.net.URI fromURI = new java.net.URI("file://" + fromPath + "/");
            java.net.URI toURI = new java.net.URI("file://" + toPath);
            java.net.URI relativeURI = fromURI.relativize(toURI);
            String relativePath = relativeURI.getPath();
            
            System.out.println("Calculated relative path: " + relativePath);
            return relativePath;
        } catch (Exception e) {
            System.out.println("Error calculating relative path, using direct path: " + e.getMessage());
            // Fallback to direct path if relative path calculation fails
            return to.getName();
        }
    }

    private String copyVideoToReportsDirectory(String videoPath, String testName) {
        try {
            // Create reports directory if it doesn't exist
            File reportsDir = new File("./reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
                System.out.println("Created reports directory: " + reportsDir.getAbsolutePath());
            }
            
            // Create videos directory if it doesn't exist
            File videosDir = new File("./reports/videos");
            if (!videosDir.exists()) {
                videosDir.mkdirs();
                System.out.println("Created videos directory: " + videosDir.getAbsolutePath());
            }
            
            // Copy video to reports directory
            String reportVideoPath = "./reports/videos/" + testName + ".webm";
            java.nio.file.Files.copy(
                java.nio.file.Paths.get(videoPath), 
                java.nio.file.Paths.get(reportVideoPath),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            
            System.out.println("Copied video to reports directory: " + reportVideoPath);
            return reportVideoPath;
        } catch (Exception e) {
            System.err.println("Failed to copy video to reports directory: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
