package com.qa.opencart.factory;
import com.microsoft.playwright.*;
import com.microsoft.playwright.BrowserType.LaunchOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Arrays;

public class PlaywrightFactory {

    private Browser browser;
    private Playwright playwright;
    private BrowserContext browserContext;
    private Page page;
    private Properties prop;
    private String currentVideoTimestamp;

    public Page initBrowser(Properties prop) {
        String browserName = prop.getProperty("browser").trim();
        System.out.println("Browser name is : " + browserName);
        
        playwright = Playwright.create();
        
        LaunchOptions launchOptions = new LaunchOptions()
            .setHeadless(Boolean.parseBoolean(prop.getProperty("headless", "false")))
            .setSlowMo(Double.parseDouble(prop.getProperty("slowMo", "0")))
            .setArgs(Arrays.asList("--start-maximized"));

        switch (browserName.toLowerCase()) {
            case "chromium":
                browser = playwright.chromium().launch(launchOptions);
                break;
            case "firefox":
                browser = playwright.firefox().launch(launchOptions);
                break;
            case "safari":
                browser = playwright.webkit().launch(launchOptions);
                break;
            case "chrome":
                launchOptions.setChannel("chrome");
                browser = playwright.chromium().launch(launchOptions);
                break;
            default:
                System.out.println("Please pass the right browser name......");
                break;
        }

        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
            .setViewportSize(null) // For maximized window
            .setRecordVideoDir(Boolean.parseBoolean(prop.getProperty("recordVideo", "false")) ? 
                getVideoRecordingPath() : null)
            .setRecordHarPath(Boolean.parseBoolean(prop.getProperty("recordHar", "false")) ? 
                Paths.get("test-results/har/trace.har") : null);

        browserContext = browser.newContext(contextOptions);
        
        // Start tracing if enabled
        if (Boolean.parseBoolean(prop.getProperty("tracing", "false"))) {
            browserContext.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));
        }

        page = browserContext.newPage();
        page.navigate(prop.getProperty("url").trim());
        return page;
    }

    /**
     * Creates a timestamped directory path for video recordings
     * @return Path object with the video directory path including date and time
     */
    private Path getVideoRecordingPath() {
        // Create a formatter for the date and time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        // Get the current date and time and format it
        currentVideoTimestamp = LocalDateTime.now().format(formatter);
        
        // Create the path with timestamp
        String videoDir = "test-results/videos/" + currentVideoTimestamp;
        System.out.println("Video recording path: " + videoDir);
        
        return Paths.get(videoDir);
    }
    
    /**
     * Gets the expected video file path for a specific test
     * This can be used to link videos to specific test reports
     * 
     * @param testName Name of the test
     * @return Path to the video file (or null if video recording is disabled)
     */
    public String getVideoFilePath(String testName) {
        if (!Boolean.parseBoolean(prop.getProperty("recordVideo", "false")) || currentVideoTimestamp == null) {
            return null;
        }
        
        // Create path to the video directory
        String videoDir = "test-results/videos/" + currentVideoTimestamp;
        try {
            // Get the first .webm file in the directory - Playwright may name it differently
            java.io.File videoDirectory = new java.io.File(videoDir);
            if (videoDirectory.exists() && videoDirectory.isDirectory()) {
                java.io.File[] videoFiles = videoDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".webm"));
                if (videoFiles != null && videoFiles.length > 0) {
                    return videoDir + "/" + videoFiles[0].getName();
                }
            }
        } catch (Exception e) {
            System.out.println("Error finding video file: " + e.getMessage());
        }
        
        // Fallback to default naming pattern if no file found
        return videoDir + "/page-1.webm";
    }

    public Properties init_prop() {
        try {
            FileInputStream ip = new FileInputStream("./src/test/resources/config/config.properties");
            prop = new Properties();
            prop.load(ip);
        } catch (IOException e) {
            throw new RuntimeException("Error reading config.properties file", e);
        }
        return prop;
    }

    public void closeContext() {
        if (browserContext != null) {
            if (Boolean.parseBoolean(prop.getProperty("tracing", "false"))) {
                browserContext.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("test-results/trace.zip")));
            }
            browserContext.close();
        }
    }

    public void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
    }

    public void closePlaywright() {
        if (playwright != null) {
            playwright.close();
        }
    }
}
