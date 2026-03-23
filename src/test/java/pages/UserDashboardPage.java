package pages;

import com.codeborne.selenide.Condition;
import io.appium.java_client.MobileBy;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class UserDashboardPage {

    private static final String DASHBOARD_TITLE = "//android.widget.TextView[@text='Dashboard']";
    private static final String DEPOSIT_BUTTON = "//android.widget.TextView[@text='Deposit Money']";
    private static final String MAKE_TRANSFER_BUTTON = "//android.widget.TextView[@text='Make a Transfer']";

    @Step("Check dashboard is loaded")
    public UserDashboardPage checkDashboardLoaded() {
        $(MobileBy.xpath(DASHBOARD_TITLE)).should(Condition.visible);
        return this;
    }

    @Step("Open deposit flow")
    public DepositPage depositMoney() {
        $(MobileBy.xpath(DEPOSIT_BUTTON)).should(Condition.visible).click();
        return new DepositPage();
    }

    @Step("Open Make a Transfer flow")
    public TransferPage makeTransfer() {
        $(MobileBy.xpath(MAKE_TRANSFER_BUTTON)).should(Condition.visible).click();
        return new TransferPage();
    }
}
