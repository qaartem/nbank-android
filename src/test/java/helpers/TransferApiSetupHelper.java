package helpers;

import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.TransferTestData;
import utils.TestDataGenerator;

public final class TransferApiSetupHelper {

    private TransferApiSetupHelper() {
    }

    public static TransferTestData prepareTransferTestData() {
        CreateUserRequest senderRequest = TestDataGenerator.randomCreateUserRequest();
        UserApiHelper.createUser(senderRequest);
        CreateAccountResponse senderAccount = AccountApiHelper.createAccount(
                senderRequest.getUsername(),
                senderRequest.getPassword()
        );

        CreateUserRequest receiverRequest = TestDataGenerator.randomCreateUserRequest();
        UserApiHelper.createUser(receiverRequest);
        CreateAccountResponse receiverAccount = AccountApiHelper.createAccount(
                receiverRequest.getUsername(),
                receiverRequest.getPassword()
        );

        return TransferTestData.builder()
                .senderUsername(senderRequest.getUsername())
                .senderPassword(senderRequest.getPassword())
                .senderAccountId(senderAccount.getId())
                .senderAccountNumber(senderAccount.getAccountNumber())
                .senderInitialBalance(senderAccount.getBalance() != null ? senderAccount.getBalance() : 0f)
                .receiverUsername(receiverRequest.getUsername())
                .receiverPassword(receiverRequest.getPassword())
                .receiverAccountId(receiverAccount.getId())
                .receiverAccountNumber(receiverAccount.getAccountNumber())
                .build();
    }
}
