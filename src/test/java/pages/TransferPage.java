package pages;

import com.codeborne.selenide.Condition;
import io.appium.java_client.MobileBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class TransferPage {
    private static final Logger log = LoggerFactory.getLogger(TransferPage.class);

    private static final String TITLE = "//android.widget.TextView[contains(@text, 'Make a Transfer')]";
    private static final String SENDER_ACCOUNT_SPINNER = "//*[contains(@resource-id, 'android:id/text1')]";
    private static final String SPINNER_OPTION_IN_LIST = "//android.widget.CheckedTextView[@text='%s']";
    private static final String RECIPIENT_NAME_INPUT = "//android.widget.EditText[contains(@text, 'Recipient Name')]";
    private static final String RECEIVER_ACCOUNT_ID_INPUT = "//android.widget.EditText[contains(@text, 'Receiver Account ID')]";
    private static final String AMOUNT_INPUT = "//android.widget.EditText[contains(@text, 'Amount')]";
    private static final String CONFIRM_SWITCH = "//android.widget.Switch";
    private static final String SEND_TRANSFER_BUTTON = "//android.widget.Button[contains(@content-desc, 'SEND TRANSFER')]";

    @Step("Wait for Transfer screen to load")
    public TransferPage checkTransferScreenLoaded() {
        getWebDriver().findElement(MobileBy.xpath(TITLE));
        return this;
    }

    @Step("Select sender account: {accountNumber} (ID: {accountId})")
    public TransferPage selectSenderAccount(String accountNumber, Long accountId) {
        $(MobileBy.xpath(SENDER_ACCOUNT_SPINNER)).should(Condition.visible).click();
        String optionText = accountNumber + " (ID: " + accountId + ")";
        String optionXpath = String.format(SPINNER_OPTION_IN_LIST, optionText);
        $(MobileBy.xpath(optionXpath)).should(Condition.visible).click();
        return this;
    }

    @Step("Enter recipient name: {recipientName}")
    public TransferPage enterRecipientName(String recipientName) {
        WebElement recipientNameElement = getWebDriver().findElement(MobileBy.xpath(RECIPIENT_NAME_INPUT));
        recipientNameElement.clear();
        recipientNameElement.sendKeys(recipientName);
        return this;
    }

    @Step("Enter receiver account ID: {receiverAccountId}")
    public TransferPage enterReceiverAccountId(String receiverAccountId) {
        WebElement receiverAccountIdElement = getWebDriver().findElement(MobileBy.xpath(RECEIVER_ACCOUNT_ID_INPUT));
        receiverAccountIdElement.clear();
        receiverAccountIdElement.sendKeys(receiverAccountId);
        return this;
    }

    @Step("Enter amount: {amount}")
    public TransferPage enterAmount(float amount) {
        WebElement amountElement = getWebDriver().findElement(MobileBy.xpath(AMOUNT_INPUT));
        amountElement.clear();
        amountElement.sendKeys(String.valueOf(amount));
        return this;
    }

    @Step("Confirm details are correct")
    public TransferPage confirmDetails() {
        WebElement confirmSwitchElement = getWebDriver().findElement(MobileBy.xpath(CONFIRM_SWITCH));
        confirmSwitchElement.click();
        return this;
    }

    @Step("Send transfer")
    public TransferPage sendTransfer() {
        WebElement sendTransferButtonElement = getWebDriver().findElement(MobileBy.xpath(SEND_TRANSFER_BUTTON));
        sendTransferButtonElement.click();
        return this;
    }

    @Step("Perform transfer: sender={senderAccountNumber} (ID: {senderAccountId}), receiverId={receiverAccountId}, amount={amount}")
    public TransferPage performTransfer(String senderAccountNumber, Long senderAccountId, String receiverAccountId, float amount, String recipientName) {
        log.debug("Transfer: from {} (ID: {}) to {} amount {}", senderAccountNumber, senderAccountId, receiverAccountId, amount);
        checkTransferScreenLoaded();
        selectSenderAccount(senderAccountNumber, senderAccountId);
        if (recipientName != null && !recipientName.isEmpty()) {
            enterRecipientName(recipientName);
        }
        checkTransferScreenLoaded();
        enterReceiverAccountId(receiverAccountId);
        enterAmount(amount);
        confirmDetails();
        sendTransfer();
        return this;
    }
}
