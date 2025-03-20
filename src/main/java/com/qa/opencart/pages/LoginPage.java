package com.qa.opencart.pages;

import com.microsoft.playwright.Page;

public class LoginPage extends BasePage {
    // Locators
    private String emailInput = "#input-email";
    private String passwordInput = "#input-password";
    private String loginButton = "input[value='Login']";
    private String forgotPasswordLink = "a:has-text('Forgotten Password')";
    private String loginErrorMessage = ".alert-danger";

    public LoginPage(Page page) {
        super(page);
    }

    public String getLoginPageTitle() {
        return getPageTitle();
    }

    public void enterEmail(String email) {
        type(emailInput, email);
    }

    public void enterPassword(String password) {
        type(passwordInput, password);
    }

    public void clickLoginButton() {
        click(loginButton);
    }

    public void doLogin(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLoginButton();
    }

    public boolean isForgotPasswordLinkExist() {
        return isElementVisible(forgotPasswordLink);
    }

    public String getLoginErrorMessage() {
        return getText(loginErrorMessage);
    }

    public void clickForgotPasswordLink() {
        click(forgotPasswordLink);
    }
}
