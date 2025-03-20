package com.qa.opencart.pages;

import com.microsoft.playwright.Page;
import java.io.File;

public class HomePage extends BasePage {
    // Locators
    private String search = "input[name='search']";
    private String searchIcon = "div#search button";
    private String searchPageHeader = "div#content h1";
    private String myAccountDropdown = "a.dropdown-toggle:has-text('My Account')";
    private String loginLink = "ul.dropdown-menu li:has-text('Login')";
    private String registerLink = "ul.dropdown-menu li:has-text('Register')";

    public HomePage(Page page) {
        super(page);
    }

    public String getHomePageTitle() {
        return getPageTitle();
    }

    public String getHomePageURL() {
        return getPageUrl();
    }

    public SearchPage doSearch(String productName) {
        type(search, productName);
        click(searchIcon);
        return new SearchPage(page);
    }

    public void clickOnMyAccount() {
        click(myAccountDropdown);
    }

    public LoginPage navigateToLogin() {
        clickOnMyAccount();
        click(loginLink);
        return new LoginPage(page);
    }

    public void navigateToRegister() {
        clickOnMyAccount();
        click(registerLink);
    }

    public void takeScreenshot(String filePath) {
        try {
            // Ensure the directory exists
            File screenshotFile = new File(filePath);
            File parentDir = screenshotFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            // Take the screenshot
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(java.nio.file.Paths.get(filePath))
                .setFullPage(true));
            
            System.out.println("Screenshot captured: " + filePath);
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
