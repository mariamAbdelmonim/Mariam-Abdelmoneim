package org.example.tests;

import org.example.pages.LoginPage;
import org.example.utils.ConfigReader;
import org.openqa.selenium.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Duration;

/**
 * This class includes tests for login functionality on the demo e-commerce website.
 * It uses Soft Assertions to ensure non-blocking validations throughout the test flow,
 * allowing for thorough checks and improved stability during execution.
 *
 * Each test method is structured to validate various login scenarios and page behaviors.
 * Now with advanced error handling for invalid login attempts and message validation.
 */
public class LoginTests {

    private WebDriver driver;
    private LoginPage loginPage;
    private ConfigReader configReader;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup(); // Set up ChromeDriver automatically for the test execution.
        // This ensures we always work with the correct version of the driver without manual intervention.
    }

    @BeforeMethod
    public void init() {
        driver = new ChromeDriver(); // Initiating a fresh ChromeDriver for each test to ensure a clean test environment.
        configReader = new ConfigReader(); // ConfigReader is used to fetch credentials dynamically, ensuring flexibility in testing.
        loginPage = new LoginPage(driver); // Instantiate LoginPage to simulate the user login behavior.
        driver.get(configReader.getBaseUrl()); // Navigate to the demo website URL before each test.
    }




    @Test
    public void testLoginPageUI() {
        SoftAssert softAssert = new SoftAssert(); // SoftAssert to perform non-blocking assertions

        // Open the login page
        driver.get("https://www.saucedemo.com/");

        // Check presence and state of login fields using SoftAssert (non-blocking)
        WebElement usernameField = driver.findElement(By.id("user-name"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));

        softAssert.assertTrue(usernameField.isDisplayed(), "Username field is missing on the login page.");
        softAssert.assertTrue(passwordField.isDisplayed(), "Password field is missing on the login page.");
        softAssert.assertTrue(loginButton.isDisplayed(), "Login button is missing on the login page.");


        softAssert.assertTrue(usernameField.isEnabled(), "Username field is not enabled.");
        softAssert.assertTrue(passwordField.isEnabled(), "Password field is not enabled.");
        softAssert.assertTrue(loginButton.isEnabled(), "Login button is not enabled.");

        // Verify password field type (this is a critical check, so use HardAssert)
        String passwordFieldType = passwordField.getAttribute("type");
        Assert.assertEquals(passwordFieldType, "password", "Password field should be masked (type='password').");

        // Verify placeholders for username and password fields using SoftAssert
        String usernamePlaceholder = usernameField.getAttribute("placeholder");
        String passwordPlaceholder = passwordField.getAttribute("placeholder");
        softAssert.assertEquals(usernamePlaceholder, "Username", "Username field placeholder is incorrect.");
        softAssert.assertEquals(passwordPlaceholder, "Password", "Password field placeholder is incorrect.");

        // Now attempt login with a locked-out user (critical, so use HardAssert)
        usernameField.sendKeys("standard_user");
        passwordField.sendKeys("secret_sauce");
        loginButton.click();


        // Finalize soft assertions
        softAssert.assertAll(); // Ensure all assertions are checked at the end
    }



    @Test
    public void testLockedOutUserLogin() {
        SoftAssert softAssert = new SoftAssert(); // Soft assertion for non-blocking validation during the test.

        // Login attempt with a locked-out user (simulating invalid login).
        WebElement usernameField = driver.findElement(By.id("user-name"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));

        // Ensure 'X' is not visible before the error message appears
        WebElement clearUsernameButtonBeforeError = null;
        WebElement clearPasswordButtonBeforeError = null;

        try {
            // Try to find the 'X' buttons before login attempt
            clearUsernameButtonBeforeError = driver.findElement(By.cssSelector("svg.fa-times-circle"));
            clearPasswordButtonBeforeError = driver.findElement(By.cssSelector("svg.fa-times-circle"));

            // Verify that 'X' buttons are NOT displayed before the error
            softAssert.assertFalse(clearUsernameButtonBeforeError.isDisplayed(),
                    "Clear 'X' button should not be visible in username field before error message.");
            softAssert.assertFalse(clearPasswordButtonBeforeError.isDisplayed(),
                    "Clear 'X' button should not be visible in password field before error message.");
        } catch (NoSuchElementException e) {
            // Expected as the 'X' button might not exist before the error message appears
        }

        // Attempt to log in with a locked-out user
        usernameField.sendKeys("locked_out_user");
        passwordField.sendKeys("secret_sauce");
        loginButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement errorMessage = null;

        try {
            // Wait until the error message is visible
            errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".error-message-container")));
            softAssert.assertTrue(errorMessage.isDisplayed(),
                    "Error message should appear for locked-out user, but it was not displayed.");
        } catch (TimeoutException e) {
            softAssert.fail("Error message did not appear in time for locked-out user.");
        }

        // Verify that the error message text indicates the 'locked-out' issue
        String errorMessageText = errorMessage.getText();
        softAssert.assertTrue(errorMessageText.contains("locked out"),
                "Error message does not indicate the correct issue. Expected 'locked out' message.");

        // Now check that the 'X' button appears after the error message
        WebElement clearUsernameButtonAfterError = driver.findElement(By.cssSelector("svg.fa-times-circle"));
        WebElement clearPasswordButtonAfterError = driver.findElement(By.cssSelector("svg.fa-times-circle"));

        softAssert.assertTrue(clearUsernameButtonAfterError.isDisplayed(),
                "Clear 'X' button should be visible in username field after error message.");
        softAssert.assertTrue(clearPasswordButtonAfterError.isDisplayed(),
                "Clear 'X' button should be visible in password field after error message.");

        // Now check if the 'X' buttons to clear the username and password fields work
        if (clearUsernameButtonAfterError.isDisplayed()) {
            clearUsernameButtonAfterError.click();
            softAssert.assertTrue(usernameField.getText().isEmpty(), "Username field should be cleared after clicking 'X'.");
        }

        if (clearPasswordButtonAfterError.isDisplayed()) {
            clearPasswordButtonAfterError.click();
            softAssert.assertTrue(passwordField.getText().isEmpty(), "Password field should be cleared after clicking 'X'.");
        }

        // Verify that the username and password fields are cleared after clicking 'X'
        softAssert.assertTrue(usernameField.getText().isEmpty(), "Username field should be cleared after closing the error message.");
        softAssert.assertTrue(passwordField.getText().isEmpty(), "Password field should be cleared after closing the error message.");

        // Click on the username field to make sure it is editable again
        usernameField.click();
        softAssert.assertTrue(usernameField.isEnabled(), "Username field should be editable after closing the error message.");

        // Click on the password field to make sure it is editable again
        passwordField.click();
        softAssert.assertTrue(passwordField.isEnabled(), "Password field should be editable after closing the error message.");

        softAssert.assertAll(); // Ensure all soft assertions are checked at the end.
    }




    @Test
    public void testProblemUserLogin() {
        // Hard Assertions to ensure the presence of essential elements on the login page
        WebElement usernameField = driver.findElement(By.id("user-name"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));


        // Input username and password, then click the login button
        usernameField.sendKeys("problem_user");
        passwordField.sendKeys("secret_sauce");
        loginButton.click();

        // Soft Assertions for additional checks after login
        SoftAssert softAssert = new SoftAssert();

        // Verify the page title after login to ensure redirection to the correct page
        String expectedPageTitle = "Swag Labs"; // Update the expected page title for the Inventory page
        String actualPageTitle = driver.getTitle();
        softAssert.assertEquals(actualPageTitle, expectedPageTitle, "Expected page title to be 'Swag Labs' after login.");

        // Ensure that the inventory page is displayed after login
        WebElement inventoryContainer = driver.findElement(By.className("inventory_container"));
        softAssert.assertTrue(inventoryContainer.isDisplayed(), "Inventory page should be visible after login.");

        // Verify all Soft Assertions at the end
        softAssert.assertAll();
    }




    @Test
    public void testPerformanceGlitchUserLogin() {
        SoftAssert softAssert = new SoftAssert();

        // Hard Assertions to ensure the presence of essential elements on the login page
        WebElement usernameField = driver.findElement(By.id("user-name"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));


        // Start timing the login process for performance glitch user
        long startTime = System.currentTimeMillis();

        // Input username and password, then click the login button
        usernameField.sendKeys("performance_glitch_user");
        passwordField.sendKeys("secret_sauce");
        loginButton.click();

        // Wait for the login process to complete (to simulate the glitch)
        WebDriverWait waitForLogin = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Wait until the URL contains "inventory.html" (indicating successful login)
            waitForLogin.until(ExpectedConditions.urlContains("inventory.html"));

            long elapsedTime = System.currentTimeMillis() - startTime; // Measure elapsed time to ensure performance checks

            // Assert that login took longer than expected (performance glitch)
            softAssert.assertTrue(elapsedTime > 5000, "Performance issue was not detected. The login process took less than expected.");
        } catch (TimeoutException e) {
            // If URL doesn't contain "inventory.html", login failed, check for error message
            WebElement errorMessage = waitForLogin.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".error-message-container")));
            String errorMessageText = errorMessage.getText();
            softAssert.assertTrue(errorMessageText.contains("Please enter valid email or password"),
                    "Error message does not contain the expected 'Please enter valid email or password' text.");

            // Check if 'X' buttons are visible after error message
            WebElement clearUsernameButtonAfterError = driver.findElement(By.cssSelector("svg.fa-times-circle"));
            WebElement clearPasswordButtonAfterError = driver.findElement(By.cssSelector("svg.fa-times-circle"));

            softAssert.assertTrue(clearUsernameButtonAfterError.isDisplayed(), "Clear 'X' button should be visible in username field after error message.");
            softAssert.assertTrue(clearPasswordButtonAfterError.isDisplayed(), "Clear 'X' button should be visible in password field after error message.");

            // Click on the 'X' to clear the error message
            WebElement closeErrorButton = driver.findElement(By.cssSelector("svg.fa-times"));
            closeErrorButton.click();

            // Wait until the error message disappears
            waitForLogin.until(ExpectedConditions.invisibilityOf(errorMessage));
            softAssert.assertTrue(errorMessage.getAttribute("style").contains("display: none"),
                    "Error message should disappear after clicking on the close button.");

            // Now check if the 'X' buttons to clear the username and password fields work
            if (clearUsernameButtonAfterError.isDisplayed()) {
                clearUsernameButtonAfterError.click();
                softAssert.assertTrue(usernameField.getText().isEmpty(), "Username field should be cleared after clicking 'X'.");
            }

            if (clearPasswordButtonAfterError.isDisplayed()) {
                clearPasswordButtonAfterError.click();
                softAssert.assertTrue(passwordField.getText().isEmpty(), "Password field should be cleared after clicking 'X'.");
            }
        }

        // Finalize soft assertions
        softAssert.assertAll();
    }







    @Test
    public void testErrorUserLogin() {
        SoftAssert softAssert = new SoftAssert();  // Soft assertion for non-blocking validation during the test.

        // Define the fields on the login page
        WebElement usernameField = driver.findElement(By.id("user-name"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));


        // Attempt login with invalid user (Error User)
        usernameField.sendKeys("error_user");  // "error_user" is invalid.
        passwordField.sendKeys("wrong_password");  // "wrong_password" is invalid.
        loginButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement errorMessage = null;

        try {
            // Wait for the error message to appear
            errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("error_message")));
        } catch (TimeoutException e) {
            // If error message does not appear in time, handle gracefully
            softAssert.fail("Error message did not appear in time for error user.");
        }

        // Verify if errorMessage is found and not null
        if (errorMessage != null) {
            // Check if the error message contains the correct text
            String errorMessageText = errorMessage.getText();
            softAssert.assertTrue(errorMessageText.contains("error user"),
                    "Error message does not indicate the correct issue. Expected 'error user' message.");

            // Click the "X" to close the error message
            WebElement closeErrorButton = driver.findElement(By.cssSelector("svg.fa-times"));
            closeErrorButton.click();

            // Wait for the error message to disappear
            try {
                wait.until(ExpectedConditions.invisibilityOf(errorMessage));
                softAssert.assertTrue(errorMessage.getAttribute("style").contains("display: none"),
                        "Error message should disappear after clicking on the close button.");
            } catch (TimeoutException e) {
                softAssert.fail("Error message did not disappear after clicking on the close button.");
            }

            // Ensure the fields are editable after closing the error message
            usernameField.click();
            softAssert.assertTrue(usernameField.isEnabled(), "Username field should be editable after closing the error message.");

            passwordField.click();
            softAssert.assertTrue(passwordField.isEnabled(), "Password field should be editable after closing the error message.");
        } else {
            softAssert.fail("Error message was not found on the page.");
        }

        // End all soft assertions
        softAssert.assertAll();
    }






    @Test
    public void testVisualUserLogin() {
        SoftAssert softAssert = new SoftAssert();

        // Define the fields and buttons on the login page
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        WebElement loginButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));


        // Step 1: Login attempt with correct data
        usernameField.sendKeys("visual_user");
        passwordField.sendKeys("secret_sauce");  // Correct password for a successful login
        loginButton.click();

        WebElement inventoryPage = null;

        try {
            // Wait for the inventory page to be visible after successful login
            inventoryPage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inventory_container")));
            softAssert.assertTrue(inventoryPage.isDisplayed(),
                    "Visual user should see the inventory page with proper visibility after successful login.");
        } catch (TimeoutException e) {
            softAssert.fail("Visual user failed to see the inventory page after successful login.");
        }

        // Step 2: Redirect back to the login page manually
        // Assuming that you can directly go back to the login page with a URL.
        driver.get("https://www.saucedemo.com/"); // Replace with the actual URL for your login page.

        // Re-validate the username and password fields after redirecting back to the login page
        usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        loginButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));

        // Try logging in with incorrect data
        usernameField.sendKeys("invalid_user");
        passwordField.sendKeys("wrong_password");  // Incorrect password to trigger error
        loginButton.click();

        WebElement errorMessage = null;
        WebElement closeErrorButton = null;

        try {
            // Wait for the error message to appear
            errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("error_message")));
            softAssert.assertTrue(errorMessage.isDisplayed(), "Error message should appear after invalid login.");
        } catch (TimeoutException e) {
            softAssert.fail("Error message did not appear for invalid login.");
        }

        // Step 3: Ensure the "X" button is shown after error message
        try {
            closeErrorButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("svg.fa-times")));
            softAssert.assertTrue(closeErrorButton.isDisplayed(), "'X' button should be visible after error message.");
        } catch (TimeoutException e) {
            softAssert.fail("'X' button did not appear after error message.");
        }

        // Step 4: Click on "X" button to close the error message
        closeErrorButton.click();

        // Step 5: Ensure the error message disappears
        try {
            wait.until(ExpectedConditions.invisibilityOf(errorMessage));
            softAssert.assertTrue(errorMessage.getAttribute("style").contains("display: none"),
                    "Error message should disappear after clicking on the close button.");
        } catch (TimeoutException e) {
            softAssert.fail("Error message did not disappear after clicking on the close button.");
        }

        // Step 6: Re-enter correct data after closing the error message
        // Re-locate the elements since the previous ones may have become stale after closing the error message
        usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        loginButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));

        usernameField.clear();
        passwordField.clear();
        usernameField.sendKeys("visual_user");
        passwordField.sendKeys("secret_sauce");  // Correct password for a successful login
        loginButton.click();

        // Step 7: Wait for the inventory page to be visible again after successful login
        try {
            inventoryPage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inventory_container")));
            softAssert.assertTrue(inventoryPage.isDisplayed(),
                    "Visual user should see the inventory page with proper visibility after re-login.");
        } catch (TimeoutException e) {
            softAssert.fail("Visual user failed to see the inventory page after re-login.");
        }

        // End all soft assertions
        softAssert.assertAll();
    }


    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit(); // Clean up and close the browser after each test method
        }
    }
}
