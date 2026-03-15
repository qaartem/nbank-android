package tests;

import helpers.AccountApiHelper;
import helpers.UserApiHelper;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.GetAccountDetailsResponse;
import models.Transaction;
import models.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.DepositPage;
import pages.LoginPage;
import pages.UserDashboardPage;
import utils.TestDataGenerator;

import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

public class BankTests extends BaseTest {

    @Test
    @DisplayName("User can login with valid credentials")
    public void userCanLogin() {
        new LoginPage()
                .enterUsername(ADMIN.getUsername())
                .enterPassword(ADMIN.getPassword())
                .clickLoginButton()
                .checkAdminPanelLoaded();
    }

    @Test
    @DisplayName("User can deposit money")
    void checkThatUserCanDepositMoney() {
        CreateUserRequest userRequest = UserApiHelper.createUser(TestDataGenerator.randomCreateUserRequest());
        String username = userRequest.getUsername();
        String password = userRequest.getPassword();

        CreateAccountResponse newAccount = AccountApiHelper.createAccount(username, password);

        float amount = Math.round(ThreadLocalRandom.current().nextFloat(10, 100) * 100) / 100f;

        new LoginPage()
                .enterUsername(username)
                .enterPassword(password)
                .clickLoginButtonAndOpenUserDashboard()
                .checkDashboardLoaded()
                .depositMoney()
                .depositMoney(newAccount.getId(), newAccount.getAccountNumber(), amount)
                .checkAlertMessageAndAmountAndAccountNumberAccept(
                        "Successfully deposited",
                        amount,
                        newAccount.getAccountNumber()
                );

        GetAccountDetailsResponse accountDetails = AccountApiHelper.getCustomerAccounts(username, password).stream()
                .filter(a -> newAccount.getAccountNumber().equals(a.getAccountNumber()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Account not found: " + newAccount.getAccountNumber()));

        assertThat(accountDetails.getAccountNumber())
                .isEqualTo(newAccount.getAccountNumber());

        assertThat(accountDetails.getTransactions())
                .isNotEmpty();

        Transaction lastTransaction = accountDetails.getTransactions().getFirst();

        Transaction expectedTransaction = Transaction.builder()
                .type(TransactionType.DEPOSIT.toString())
                .relatedAccountId(accountDetails.getId())
                .amount((long) amount)
                .build();

        assertThat(lastTransaction)
                .usingRecursiveComparison()
                .ignoringFields("id", "timestamp")
                .isEqualTo(expectedTransaction);

        float initialBalance = newAccount.getBalance() != null ? newAccount.getBalance() : 0f;
        assertThat(accountDetails.getBalance())
                .isEqualTo(initialBalance + lastTransaction.getAmount().floatValue());
    }
}