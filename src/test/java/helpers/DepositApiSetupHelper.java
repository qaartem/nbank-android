package helpers;

import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.DepositTestData;
import utils.TestDataGenerator;

public final class DepositApiSetupHelper {

    private DepositApiSetupHelper() {
    }

    public static DepositTestData prepareDepositTestData() {
        CreateUserRequest userRequest = TestDataGenerator.randomCreateUserRequest();
        UserApiHelper.createUser(userRequest);

        CreateAccountResponse account = AccountApiHelper.createAccount(
                userRequest.getUsername(),
                userRequest.getPassword()
        );

        Float balance = account.getBalance() != null ? account.getBalance() : 0f;
        return DepositTestData.builder()
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .accountId(account.getId())
                .accountNumber(account.getAccountNumber())
                .initialBalance(balance)
                .build();
    }
}
