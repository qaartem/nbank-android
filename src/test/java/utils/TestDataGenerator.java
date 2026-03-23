package utils;

import models.CreateUserRequest;

import java.util.concurrent.ThreadLocalRandom;

public class TestDataGenerator {

    private static final String ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String PASSWORD_SPEC = "$%&";

    public static CreateUserRequest randomCreateUserRequest() {
        String username = randomAlphanumeric(3, 15);
        String password = randomPassword();
        return CreateUserRequest.builder().username(username).password(password).build();
    }

    private static String randomAlphanumeric(int minLen, int maxLen) {
        int len = ThreadLocalRandom.current().nextInt(minLen, maxLen + 1);
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ALPHANUMERIC.charAt(ThreadLocalRandom.current().nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    private static String randomPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) sb.append((char) ('A' + ThreadLocalRandom.current().nextInt(26)));
        for (int i = 0; i < 4; i++) sb.append((char) ('a' + ThreadLocalRandom.current().nextInt(26)));
        for (int i = 0; i < 3; i++) sb.append((char) ('0' + ThreadLocalRandom.current().nextInt(10)));
        for (int i = 0; i < 2; i++) sb.append(PASSWORD_SPEC.charAt(ThreadLocalRandom.current().nextInt(PASSWORD_SPEC.length())));
        return sb.toString();
    }
}
