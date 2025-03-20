package com.qa.opencart.pages;

import com.microsoft.playwright.Page;

public class SearchPage {
    private Page page;

    // 1. String Locators - OR
    private String searchResults = "div.product-layout";
    private String searchPageHeader = "div#content h1";

    // 2. page constructor:
    public SearchPage(Page page) {
        this.page = page;
    }

    // 3. page actions/methods:
    public String getSearchPageTitle() {
        return page.title();
    }

    public int getSearchResultsCount() {
        return page.locator(searchResults).count();
    }

    public String getSearchPageHeader() {
        return page.textContent(searchPageHeader);
    }
} 