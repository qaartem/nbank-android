package tests;

import helpers.DepositApiSetupHelper;
import helpers.DepositVerificationHelper;
import models.DepositTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pages.LoginPage;

import static io.qameta.allure.Allure.step;

@DisplayName("Deposit (API setup + UI act + API assert)")
public class DepositTest extends BaseTest {
    private static final Logger log = LoggerFactory.getLogger(DepositTest.class);

    private static final long DEPOSIT_AMOUNT = 50L;

    @Test
    @DisplayName("User can deposit money: API setup → UI deposit → API verification")
    void userCanDepositMoney() {
        log.info("Deposit test: Arrange — create user and account via API");
        DepositTestData testData = step("Create user and account via API", DepositApiSetupHelper::prepareDepositTestData);

        log.info("Deposit test: Act — login and deposit in app, amount={}", DEPOSIT_AMOUNT);
        log.debug("Login credentials: username={}, password={}", testData.getUsername(), testData.getPassword());
        step("Login and perform deposit in app", () -> {
            new LoginPage()
                    .enterUsername(testData.getUsername())
                    .enterPassword(testData.getPassword())
                    .clickLoginButtonAndOpenUserDashboard()
                    .checkDashboardLoaded()
                    .depositMoney()
                    .depositMoney(testData.getAccountId(), testData.getAccountNumber(), (float) DEPOSIT_AMOUNT);
        });

        log.info("Deposit test: Assert — verify via API");
        step("Verify balance and last transaction via API", () ->
                DepositVerificationHelper.verifyDepositSuccess(
                        testData.getUsername(),
                        testData.getPassword(),
                        testData.getAccountNumber(),
                        testData.getInitialBalance(),
                        DEPOSIT_AMOUNT
                )
        );
    }
}
