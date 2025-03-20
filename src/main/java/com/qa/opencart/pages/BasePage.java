package com.qa.opencart.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.nio.file.Paths;

public class BasePage {
    protected Page page;
    protected String screenshotsPath;

    public BasePage(Page page) {
        this.page = page;
        this.screenshotsPath = "./test-results/screenshots/";
    }

    // Wait Methods
    protected void waitForElementVisible(String selector) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
    }

    protected void waitForElementClickable(String selector) {
        Locator element = page.locator(selector);
        element.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    // Action Methods
    protected void click(String selector) {
        waitForElementClickable(selector);
        page.click(selector);
    }

    protected void type(String selector, String text) {
        waitForElementVisible(selector);
        page.fill(selector, text);
    }

    protected String getText(String selector) {
        waitForElementVisible(selector);
        return page.textContent(selector);
    }

    protected boolean isElementVisible(String selector) {
        try {
            waitForElementVisible(selector);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Screenshot Methods
    public void takeScreenshot(String name) {
        page.screenshot(new Page.ScreenshotOptions()
            .setPath(Paths.get(screenshotsPath + name + ".png"))
            .setFullPage(true));
    }

    // Navigation Methods
    protected void navigateTo(String url) {
        page.navigate(url);
    }

    protected String getPageTitle() {
        return page.title();
    }

    protected String getPageUrl() {
        return page.url();
    }
} 