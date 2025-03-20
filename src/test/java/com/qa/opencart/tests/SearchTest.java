package com.qa.opencart.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SearchTest extends BaseTest {

    @Test(dataProvider = "deviceData")
    public void searchDeviceTest(String deviceName, String expectedTitle) {
        String actualTitle = homePage.doSearch(deviceName)
                                   .getSearchPageTitle();
        Assert.assertEquals(actualTitle, expectedTitle, 
            "Search page title is not matching for device: " + deviceName);
    }
} 