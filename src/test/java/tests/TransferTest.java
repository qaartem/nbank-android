package tests;

import helpers.TransferApiSetupHelper;
import helpers.TransferVerificationHelper;
import models.TransferTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.LoginPage;

import static io.qameta.allure.Allure.step;

@DisplayName("Transfer (API setup + UI deposit + UI transfer + API assert)")
public class TransferTest extends BaseTest {
    private static final Logger log = LoggerFactory.getLogger(TransferTest.class);

    private static final float DEPOSIT_AMOUNT = 20f;
    private static final float TRANSFER_AMOUNT = 10f;

    @Test
    @DisplayName("User can transfer money: API setup → UI deposit → UI transfer → API verification")
    void userCanTransferMoney() {
        log.info("Transfer test: Arrange — create two users and two accounts via API");
        TransferTestData testData = step("Create two users and two accounts via API", TransferApiSetupHelper::prepareTransferTestData);

        log.info("Transfer test: Act — login, deposit via UI, then transfer via UI");
        step("Login, deposit money via UI, then make transfer via UI", () -> {
            new LoginPage()
                    .enterUsername(testData.getSenderUsername())
                    .enterPassword(testData.getSenderPassword())
                    .clickLoginButtonAndOpenUserDashboard()
                    .checkDashboardLoaded()
                    .depositMoney()
                    .depositMoney(testData.getSenderAccountId(), testData.getSenderAccountNumber(), DEPOSIT_AMOUNT)
                    .returnToDashboard()
                    .checkDashboardLoaded()
                    .makeTransfer()
                    .performTransfer(
                            testData.getSenderAccountNumber(),
                            testData.getSenderAccountId(),
                            String.valueOf(testData.getReceiverAccountId()),
                            TRANSFER_AMOUNT,
                            testData.getReceiverUsername()
                    );
        });

        log.info("Transfer test: Assert — verify balances via API");
        step("Verify transfer success via API", () ->
                TransferVerificationHelper.verifyTransferSuccess(
                        testData.getSenderUsername(),
                        testData.getSenderPassword(),
                        testData.getSenderAccountNumber(),
                        testData.getReceiverUsername(),
                        testData.getReceiverPassword(),
                        testData.getReceiverAccountNumber(),
                        DEPOSIT_AMOUNT,
                        TRANSFER_AMOUNT
                )
        );
    }
}
