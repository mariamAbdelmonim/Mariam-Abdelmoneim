package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * This class models the Login Page of the Saucedemo website.
 * Page Object Model (POM) is used to enhance maintainability and scalability.
 * It encapsulates all the actions and elements of the login page.
 */
public class LoginPage {

    private WebDriver driver;

    // Locators for the login page elements
    private By usernameField = By.id("user-name");  // Username input field
    private By passwordField = By.id("password");    // Password input field
    private By loginButton = By.id("login-button");  // Login button
    private By errorMessage = By.cssSelector(".error-message-container");  // Error message element

    /**
     * Constructor to initialize the driver.
     * @param driver The WebDriver instance to interact with the browser.
     */
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Method to perform login action.
     * @param username The username for login.
     * @param password The password for login.
     */
    public void login(String username, String password) {
        // Clear the username and password fields and input new values
        driver.findElement(usernameField).clear();
        driver.findElement(usernameField).sendKeys(username);
        driver.findElement(passwordField).clear();
        driver.findElement(passwordField).sendKeys(password);

        // Click the login button
        driver.findElement(loginButton).click();
    }

    /**
     * Method to verify if an error message is displayed after login failure.
     * @return True if the error message is displayed, else false.
     */
    public boolean isErrorMessageDisplayed() {
        WebElement errorElement = driver.findElement(errorMessage);
        return errorElement.isDisplayed();
    }
}
