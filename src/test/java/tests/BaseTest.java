package tests;

import static io.qameta.allure.Allure.step;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;

import config.Config;
import driver.EmulatorDriver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import helpers.ApiHelper;
import helpers.ScriptHelper;
import io.qameta.allure.selenide.AllureSelenide;
import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTest {
    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);
    private static final String START_EMULATOR_SCRIPT_NAME = "start-emulator.sh";
    private static final String STOP_EMULATOR_SCRIPT_NAME = "stop-emulator.sh";
    private static final String ANDROID_HOME = "ANDROID_HOME";

    protected User ADMIN = User.builder().username("admin").password("admin").build();

    @BeforeAll
    public static void setup() {
        log.info("BaseTest setup: enable backend logging, health check, start emulator");
        helpers.BackendLogging.install();
        ApiHelper.healthCheck();

        ScriptHelper.putEnvVariable(ANDROID_HOME, Config.getProperty("android.home"));
        ScriptHelper.execute(START_EMULATOR_SCRIPT_NAME);

        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());
        Configuration.browser = EmulatorDriver.class.getName();
        Configuration.browserSize=null;
        Configuration.timeout = 6000;
        log.info("Selenide/Appium configured, timeout={} ms", Configuration.timeout);
    }

    @BeforeEach
    public void startDriver() {
        log.debug("Opening driver (Selenide.open)");
        step("Open driver", () -> Selenide.open());
    }

    @AfterEach
    public void stopDriver() {
        log.debug("Closing driver");
        step("Close driver", Selenide::closeWebDriver);
    }

    @AfterAll
    public static void clean() {
        log.info("BaseTest clean: stopping emulator");
        ScriptHelper.execute(STOP_EMULATOR_SCRIPT_NAME);
    }
}