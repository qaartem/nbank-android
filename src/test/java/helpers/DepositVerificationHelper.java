package helpers;

import models.GetAccountDetailsResponse;
import models.Transaction;
import models.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class DepositVerificationHelper {
    private static final Logger log = LoggerFactory.getLogger(DepositVerificationHelper.class);

    private DepositVerificationHelper() {
    }

    public static void verifyDepositSuccess(
            String username,
            String password,
            String accountNumber,
            Float initialBalance,
            long depositAmount
    ) {
        log.info("Verifying deposit: accountNumber={}, expectedAmount={}, initialBalance={}", accountNumber, depositAmount, initialBalance);
        List<GetAccountDetailsResponse> accounts = AccountApiHelper.getCustomerAccounts(username, password);

        assertThat(accounts)
                .as("Customer must have at least one account")
                .isNotEmpty();

        GetAccountDetailsResponse accountDetails = accounts.stream()
                .filter(a -> accountNumber.equals(a.getAccountNumber()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Account not found in customer accounts: " + accountNumber));

        assertThat(accountDetails.getTransactions())
                .as("Transactions list should not be empty after deposit")
                .isNotEmpty();

        List<Transaction> newestFirst = TransactionListHelper.newestFirst(accountDetails.getTransactions());
        Transaction lastTransaction = newestFirst.get(0);
        assertThat(lastTransaction.getType())
                .as("Newest transaction (by id) should be the deposit just made")
                .isEqualTo(TransactionType.DEPOSIT.toString());
        assertThat(lastTransaction.getAmount())
                .as("Newest transaction amount should equal deposit amount")
                .isEqualTo(depositAmount);

        float expectedBalance = (initialBalance != null ? initialBalance : 0f) + depositAmount;
        assertThat(accountDetails.getBalance())
                .as("Balance should equal initial + deposit amount")
                .isEqualTo(expectedBalance);
        log.info("Deposit verification passed: balance={}", accountDetails.getBalance());
    }
}
