package com.qa.opencart.api;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;

import java.util.Map;
import java.util.HashMap;

public class APIClient {
    private final APIRequestContext requestContext;
    private final String baseUrl;

    public APIClient(String baseUrl) {
        this.baseUrl = baseUrl;
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        
        requestContext = Playwright
            .create()
            .request()
            .newContext(new APIRequest.NewContextOptions()
                .setBaseURL(baseUrl)
                .setExtraHTTPHeaders(headers));
    }

    public APIResponse get(String endpoint) {
        return requestContext.get(endpoint);
    }

    public APIResponse post(String endpoint, String data) {
        return requestContext.post(endpoint, RequestOptions.create().setData(data));
    }

    public APIResponse put(String endpoint, String data) {
        return requestContext.put(endpoint, RequestOptions.create().setData(data));
    }

    public APIResponse delete(String endpoint) {
        return requestContext.delete(endpoint);
    }

    public void dispose() {
        if (requestContext != null) {
            requestContext.dispose();
        }
    }
} 