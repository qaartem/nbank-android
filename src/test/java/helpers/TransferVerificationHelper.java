package helpers;

import models.GetAccountDetailsResponse;
import models.Transaction;
import models.TransactionType;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public final class TransferVerificationHelper {

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
            float initialReceiverBalance,
            float transferAmount
    ) {
        List<GetAccountDetailsResponse> senderAccounts = AccountApiHelper.getCustomerAccounts(senderUsername, senderPassword);
        GetAccountDetailsResponse senderAccount = findAccount(senderAccounts, senderAccountNumber);
        float expectedSenderBalance = initialSenderBalance - transferAmount;
        assertThat(senderAccount.getBalance())
                .as("Sender balance should be initial - transfer amount")
                .isEqualTo(expectedSenderBalance);

        List<GetAccountDetailsResponse> receiverAccounts = AccountApiHelper.getCustomerAccounts(receiverUsername, receiverPassword);
        GetAccountDetailsResponse receiverAccount = findAccount(receiverAccounts, receiverAccountNumber);
        float expectedReceiverBalance = initialReceiverBalance + transferAmount;
        assertThat(receiverAccount.getBalance())
                .as("Receiver balance should be initial + transfer amount")
                .isEqualTo(expectedReceiverBalance);

        assertThat(senderAccount.getTransactions())
                .as("Sender should have at least one transaction after transfer")
                .isNotEmpty();

        List<Transaction> senderNewestFirst = TransactionListHelper.newestFirst(senderAccount.getTransactions());
        assertThat(senderNewestFirst)
                .as("Sender transactions after sort should not be empty")
                .isNotEmpty();
        Transaction newestSenderTx = senderNewestFirst.get(0);
        assertThat(isOutgoingTransferType(newestSenderTx.getType()))
                .as("Newest sender transaction (by id) should be outgoing transfer; was type=%s, full list=%s",
                        newestSenderTx.getType(), senderNewestFirst)
                .isTrue();
        assertThat(newestSenderTx.getAmount())
                .as("Newest sender transaction should have amount")
                .isNotNull();
        assertThat(amountMatches(newestSenderTx.getAmount(), transferAmount))
                .as("Newest sender transaction amount should equal transfer amount")
                .isTrue();

        List<Transaction> receiverNewestFirst = TransactionListHelper.newestFirst(receiverAccount.getTransactions());
        assertThat(receiverNewestFirst)
                .as("Receiver should have at least one transaction after transfer")
                .isNotEmpty();
        Transaction newestReceiverTx = receiverNewestFirst.get(0);
        assertThat(isIncomingTransferType(newestReceiverTx.getType()))
                .as("Newest receiver transaction (by id) should be incoming transfer; was type=%s, full list=%s",
                        newestReceiverTx.getType(), receiverNewestFirst)
                .isTrue();
        assertThat(newestReceiverTx.getAmount())
                .as("Newest receiver transaction should have amount")
                .isNotNull();
        assertThat(amountMatches(newestReceiverTx.getAmount(), transferAmount))
                .as("Newest receiver transaction amount should equal transfer amount")
                .isTrue();
    }

    private static GetAccountDetailsResponse findAccount(List<GetAccountDetailsResponse> accounts, String accountNumber) {
        return accounts.stream()
                .filter(a -> accountNumber.equals(a.getAccountNumber()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Account not found: " + accountNumber));
    }

    private static boolean isOutgoingTransferType(String type) {
        if (type == null) {
            return false;
        }
        return Objects.equals(type, TransactionType.TRANSFER.toString())
                || Objects.equals(type, TransactionType.TRANSFER_OUT.toString());
    }

    private static boolean isIncomingTransferType(String type) {
        if (type == null) {
            return false;
        }
        return Objects.equals(type, TransactionType.TRANSFER.toString())
                || Objects.equals(type, "TRANSFER_IN");
    }

    private static boolean amountMatches(Number apiAmount, float expected) {
        return Math.abs(apiAmount.doubleValue() - expected) < 0.0001d;
    }
}
