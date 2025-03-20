package com.qa.opencart.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class HomePageTests extends BaseTest {

    @Test(description = "Verify the home page title is correct")
    public void homePageTitleTest(){
        String actualTitle = homePage.getHomePageTitle();
        System.out.println(actualTitle);
        
        // Add a custom screenshot with description
        captureAndAttachScreenshot("homePage_title", "Home Page Title Screenshot");
        
        Assert.assertEquals(actualTitle,"Your Store");
    }

    @Test(description = "Verify the home page URL is correct")
    public void homePageURLTest(){
        String actualURL = homePage.getHomePageURL();
        System.out.println(actualURL);
        
        // Add a custom screenshot with description
        captureAndAttachScreenshot("homePage_url", "Home Page URL Screenshot");
        
        Assert.assertEquals(actualURL,prop.getProperty("url"));
    }

    @Test(description = "Verify search functionality for Macbook")
    public void searchTest(){
        // Add a screenshot before search
        captureAndAttachScreenshot("before_search", "Before performing search");
        
        String header = homePage.doSearch("Macbook").getSearchPageHeader();
        System.out.println(header);
        
        // Add a screenshot after search
        captureAndAttachScreenshot("after_search", "After performing search");
        
        Assert.assertEquals(header,"Search - Macbook");
    }
}
