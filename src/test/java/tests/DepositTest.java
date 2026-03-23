package tests;

import helpers.DepositApiSetupHelper;
import helpers.DepositVerificationHelper;
import models.DepositTestData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.LoginPage;

import static io.qameta.allure.Allure.step;

@DisplayName("Deposit (API setup + UI act + API assert)")
public class DepositTest extends BaseTest {
    private static final long DEPOSIT_AMOUNT = 50L;

    @Test
    @DisplayName("User can deposit money: API setup → UI deposit → API verification")
    void userCanDepositMoney() {
        DepositTestData testData = step("Create user and account via API", DepositApiSetupHelper::prepareDepositTestData);

        step("Login and perform deposit in app", () -> {
            new LoginPage()
                    .enterUsername(testData.getUsername())
                    .enterPassword(testData.getPassword())
                    .clickLoginButtonAndOpenUserDashboard()
                    .checkDashboardLoaded()
                    .depositMoney()
                    .depositMoney(testData.getAccountId(), testData.getAccountNumber(), (float) DEPOSIT_AMOUNT);
        });

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
