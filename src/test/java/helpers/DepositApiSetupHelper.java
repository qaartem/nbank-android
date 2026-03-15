package helpers;

import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.DepositTestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.TestDataGenerator;

public final class DepositApiSetupHelper {
    private static final Logger log = LoggerFactory.getLogger(DepositApiSetupHelper.class);

    private DepositApiSetupHelper() {
    }

    public static DepositTestData prepareDepositTestData() {
        log.info("Preparing deposit test data: create user + account via API");
        CreateUserRequest userRequest = TestDataGenerator.randomCreateUserRequest();
        UserApiHelper.createUser(userRequest);

        CreateAccountResponse account = AccountApiHelper.createAccount(
                userRequest.getUsername(),
                userRequest.getPassword()
        );

        Float balance = account.getBalance() != null ? account.getBalance() : 0f;
        DepositTestData data = DepositTestData.builder()
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .accountId(account.getId())
                .accountNumber(account.getAccountNumber())
                .initialBalance(balance)
                .build();
        log.info("Deposit test data ready: user={}, accountId={}, accountNumber={}, initialBalance={}", data.getUsername(), data.getAccountId(), data.getAccountNumber(), data.getInitialBalance());
        return data;
    }
}
