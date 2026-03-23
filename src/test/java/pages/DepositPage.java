package pages;

import com.codeborne.selenide.Condition;
import io.appium.java_client.MobileBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

import com.codeborne.selenide.Selenide;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class DepositPage {
    private static final String ACCOUNT_ID_INPUT = "//android.widget.EditText[@text='Account ID']";
    private static final String ACCOUNT_NUMBER_INPUT = "//android.widget.EditText[@text='Account Number']";
    private static final String AMOUNT_INPUT = "//android.widget.EditText[@text='Amount']";
    private static final String SUBMIT_BUTTON = "//android.widget.Button[@content-desc='DEPOSIT']";
    private static final String ALERT_MESSAGE = "//android.widget.TextView[contains(@text, 'deposit')]";

    @Step("Enter account ID: {accountId}")
    public DepositPage enterAccountId(String accountId) {
        WebElement accountIdElement = getWebDriver().findElement(MobileBy.xpath(ACCOUNT_ID_INPUT));
        accountIdElement.clear();
        accountIdElement.sendKeys(accountId);
        return this;
    }

    @Step("Enter account number: {accountNumber}")
    public DepositPage enterAccountNumber(String accountNumber) {
        WebElement accountNumberElement = getWebDriver().findElement(MobileBy.xpath(ACCOUNT_NUMBER_INPUT));
        accountNumberElement.clear();
        accountNumberElement.sendKeys(accountNumber);
        return this;
    }

    @Step("Enter amount: {amount}")
    public DepositPage enterAmount(float amount) {
        WebElement amountElement = getWebDriver().findElement(MobileBy.xpath(AMOUNT_INPUT));
        amountElement.clear();
        amountElement.sendKeys(String.valueOf(amount));
        return this;
    }

    @Step("Submit deposit")
    public DepositPage submitDeposit() {
        $(MobileBy.xpath(SUBMIT_BUTTON)).should(Condition.visible).click();
        return this;
    }

    @Step("Deposit {amount} to account id={accountId}, number={accountNumber}")
    public DepositPage depositMoney(Long accountId, String accountNumber, float amount) {
        enterAccountId(accountId != null ? String.valueOf(accountId) : "");
        enterAccountNumber(accountNumber);
        enterAmount(amount);
        submitDeposit();
        return this;
    }

    @Step("Check alert shows success message and amount/account")
    public DepositPage checkAlertMessageAndAmountAndAccountNumberAccept(String expectedMessage, float amount, String accountNumber) {
        $(MobileBy.xpath(ALERT_MESSAGE)).should(Condition.visible);
        return this;
    }

    @Step("Return to dashboard")
    public UserDashboardPage returnToDashboard() {
        Selenide.back();
        return new UserDashboardPage();
    }
}
