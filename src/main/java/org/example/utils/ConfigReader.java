package org.example.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class to read configuration values from a properties file.
 * This class will load values like base URL, username, and password.
 */
public class ConfigReader {

    private Properties properties;

    /**
     * Constructor to initialize properties file.
     */
    public ConfigReader() {
        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            properties = new Properties();
            properties.load(fis);  // Load properties from the config file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to get the base URL for the website.
     * @return The base URL.
     */
    public String getBaseUrl() {
        return properties.getProperty("base.url");
    }

    /**
     * Method to get the username for login.
     * @return The username.
     */
    public String getUsername() {
        return properties.getProperty("username");
    }

    /**
     * Method to get the password for login.
     * @return The password.
     */
    public String getPassword() {
        return properties.getProperty("password");
    }
}
