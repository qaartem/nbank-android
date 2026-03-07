package driver;

import com.codeborne.selenide.WebDriverProvider;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nonnull;

import config.Config;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmulatorDriver implements WebDriverProvider {
    private String getAbsolutePath(String filePath) {
        File file = new File(filePath);
        assertTrue(file.exists(), filePath + " not found");

        return file.getAbsolutePath();
    }

    @Nonnull
    @Override
    public WebDriver createDriver(@Nonnull Capabilities capabilities) {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(Config.getProperty("platform.name"))
                .setDeviceName(Config.getProperty("device.name"))
                .setAppPackage(Config.getProperty("app.package"))
                .setAppActivity(Config.getProperty("app.activity"))
                .setApp(Config.getProperty("app"))
                .autoGrantPermissions();
        options.setCapability("noReset", false);
        options.setCapability("fullReset", false);
        options.setCapability("skipDeviceInitialization", false);
        options.setCapability("skipServerInstallation", false);

        try {
            return new AndroidDriver(new URL(Config.getProperty("appium.url")), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium URL: " + Config.getProperty("appium.url"));
        }
    }
}