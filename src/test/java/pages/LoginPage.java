package pages;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

import com.codeborne.selenide.Condition;
import io.appium.java_client.MobileBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPage {
    private static final Logger log = LoggerFactory.getLogger(LoginPage.class);
    private static final String LOGIN_TITLE = "//android.widget.TextView[@text='Login']";
    private static final String USERNAME_INPUT = "//android.widget.EditText[@text='Username']";
    private static final String PASSWORD_INPUT = "//android.widget.EditText[@text='Password']";
    private static final String LOGIN_BUTTON = "//*[@clickable='true'][.//android.widget.TextView[@text='Login']]";

    @Step("Insert username: {username}")
    public LoginPage enterUsername(String username) {
        log.debug("Entering username: {}", username);
        WebElement usernameElement = getWebDriver().findElement(MobileBy.xpath(USERNAME_INPUT));
        usernameElement.clear();
        usernameElement.sendKeys(username);
        return this;
    }

    @Step("Insert password: {password}")
    public LoginPage enterPassword(String password) {
        log.debug("Entering password: {}", password);
        WebElement passwordElement = getWebDriver().findElement(MobileBy.xpath(PASSWORD_INPUT));
        passwordElement.clear();
        passwordElement.sendKeys(password);
        return this;
    }

    public AdminPage clickLoginButton() {
        $(MobileBy.xpath(LOGIN_BUTTON)).should(Condition.visible).click();
        return new AdminPage();
    }

    public UserDashboardPage clickLoginButtonAndOpenUserDashboard() {
        log.debug("Click Login -> User Dashboard");
        $(MobileBy.xpath(LOGIN_BUTTON)).should(Condition.visible).click();
        return new UserDashboardPage();
    }
}
