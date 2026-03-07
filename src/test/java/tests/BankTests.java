package tests;

/**
 * Соответствие веб-тесту checkThatUserCanDepositMoney:
 * — @UserSession → логин через LoginPage (TEST_USER) + UserDashboardPage.checkDashboardLoaded().
 * — SessionStorage.getSteps().createAccount() → AccountApiHelper.createAccount().
 * — new UserDashboard().open().depositMoney() → Login → UserDashboardPage.depositMoney().
 * — getPage(DepositPage.class).depositMoney(...) → DepositPage.depositMoney(accountNumber, amount).
 * — checkAlertMessageAndAmountAndAccountNumberAccept → DepositPage.checkAlertMessageAndAmountAndAccountNumberAccept(...).
 * — SessionStorage.getSteps().getAccountWithSpecificNumber(...) → AccountApiHelper.getAccountWithSpecificNumber(...).
 *
 * Что нужно подставить под ваше приложение:
 * 1. В config.properties — логин/пароль TEST_USER (или создавать пользователя в @BeforeEach через админку).
 * 2. В AccountApiHelper — реальные пути API создания счёта и получения счёта по номеру.
 * 3. В UserDashboardPage и DepositPage — реальные XPath из uiautomator dump экранов пользователя и депозита.
 */
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

    /**
     * Мобильный аналог веб-теста checkThatUserCanDepositMoney:
     * админ создаёт пользователя → пользователь создаёт счёт → логин в UI → депозит → проверка через API.
     */
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
                .depositMoney(newAccount.getAccountNumber(), amount)
                .checkAlertMessageAndAmountAndAccountNumberAccept(
                        "Successfully deposited",
                        amount,
                        newAccount.getAccountNumber()
                );

        GetAccountDetailsResponse accountDetails =
                AccountApiHelper.getAccountWithSpecificNumber(newAccount.getAccountNumber(), username, password);

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