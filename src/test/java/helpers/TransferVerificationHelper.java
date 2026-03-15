package helpers;

import models.GetAccountDetailsResponse;
import models.Transaction;
import models.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class TransferVerificationHelper {
    private static final Logger log = LoggerFactory.getLogger(TransferVerificationHelper.class);

    private TransferVerificationHelper() {
    }

    public static void verifyTransferSuccess(
            String senderUsername,
            String senderPassword,
            String senderAccountNumber,
            String receiverUsername,
            String receiverPassword,
            String receiverAccountNumber,
            float initialSenderBalance,
            float transferAmount
    ) {
        log.info("Verifying transfer: senderAcc={}, receiverAcc={}, transferAmount={}, initialSenderBalance={}",
                senderAccountNumber, receiverAccountNumber, transferAmount, initialSenderBalance);

        List<GetAccountDetailsResponse> senderAccounts = AccountApiHelper.getCustomerAccounts(senderUsername, senderPassword);
        GetAccountDetailsResponse senderAccount = findAccount(senderAccounts, senderAccountNumber);
        float expectedSenderBalance = initialSenderBalance - transferAmount;
        assertThat(senderAccount.getBalance())
                .as("Sender balance should be initial - transfer amount")
                .isEqualTo(expectedSenderBalance);

        List<GetAccountDetailsResponse> receiverAccounts = AccountApiHelper.getCustomerAccounts(receiverUsername, receiverPassword);
        GetAccountDetailsResponse receiverAccount = findAccount(receiverAccounts, receiverAccountNumber);
        assertThat(receiverAccount.getBalance())
                .as("Receiver balance should equal transfer amount")
                .isEqualTo(transferAmount);

        assertThat(senderAccount.getTransactions())
                .as("Sender should have at least one transaction after transfer")
                .isNotEmpty();
        Transaction lastSenderTransaction = senderAccount.getTransactions().get(0);
        assertThat(lastSenderTransaction.getType()).isEqualTo(TransactionType.TRANSFER_OUT.toString());
        assertThat(lastSenderTransaction.getAmount()).isNotNull();
        assertThat(lastSenderTransaction.getAmount().floatValue()).isEqualTo(transferAmount);

        log.info("Transfer verification passed: senderBalance={}, receiverBalance={}",
                senderAccount.getBalance(), receiverAccount.getBalance());
    }

    private static GetAccountDetailsResponse findAccount(List<GetAccountDetailsResponse> accounts, String accountNumber) {
        return accounts.stream()
                .filter(a -> accountNumber.equals(a.getAccountNumber()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Account not found: " + accountNumber));
    }
}
