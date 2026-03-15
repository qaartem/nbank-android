package helpers;

import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.TransferTestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.TestDataGenerator;

public final class TransferApiSetupHelper {
    private static final Logger log = LoggerFactory.getLogger(TransferApiSetupHelper.class);

    private TransferApiSetupHelper() {
    }

    public static TransferTestData prepareTransferTestData() {
        log.info("Preparing transfer test data: create two users and two accounts via API");

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

        TransferTestData data = TransferTestData.builder()
                .senderUsername(senderRequest.getUsername())
                .senderPassword(senderRequest.getPassword())
                .senderAccountId(senderAccount.getId())
                .senderAccountNumber(senderAccount.getAccountNumber())
                .receiverUsername(receiverRequest.getUsername())
                .receiverPassword(receiverRequest.getPassword())
                .receiverAccountId(receiverAccount.getId())
                .receiverAccountNumber(receiverAccount.getAccountNumber())
                .build();
        log.info("Transfer test data ready: sender={}, receiver={}, senderAcc={}, receiverAcc={}",
                data.getSenderUsername(), data.getReceiverUsername(),
                data.getSenderAccountNumber(), data.getReceiverAccountNumber());
        return data;
    }
}
