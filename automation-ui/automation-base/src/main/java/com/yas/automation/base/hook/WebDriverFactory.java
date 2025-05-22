package com.yas.automation.base.hook;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

/**
 * WebDriverFactory provides utility methods to manage the lifecycle of WebDriver instances.
 * <p>
 * A new {@link WebDriver} instance is created for each test scenario and is destroyed after the test is completed.
 * Since Selenium interacts with and modifies the {@link WebDriver} during the test
 * (e.g., invoking {@link WebDriver#quit()} after scenario completion),
 * the WebDriver cannot be defined as a Spring bean to prevent unintended side effects on other tests.
 * </p>
 * <p>
 * This class is used by {@link Hooks} to initialize the WebDriver before each scenario and ensure it is destroyed afterward.
 * </p>
 */
@Component
public class WebDriverFactory {

    private final ThreadLocal<WebDriver> webDriver = new ThreadLocal<>();

    /**
     * Initializes and returns a new Chrome WebDriver instance if not already created for the current thread.
     *
     * @return a {@link WebDriver} instance specific to the current thread.
     */
    public synchronized WebDriver getChromeDriver() {
        if (webDriver.get() == null) {
            ChromeOptions options = new ChromeOptions();
            if (System.getProperty("headless") != null
                    && System.getProperty("headless").equals("true")) {
                options.addArguments("--headless");
                options.addArguments("--disable-gpu");
            }

            WebDriver webDriver = new ChromeDriver(options);
            webDriver.manage().window().maximize();
            this.webDriver.set(webDriver);
        }
        return webDriver.get();
    }

    /**
     * Destroys the WebDriver instance for the current thread.
     */
    synchronized void destroyDriver() {
        WebDriver driver = webDriver.get();
        if (driver != null) {
            driver.quit();
            webDriver.remove();
        }
    }

}
