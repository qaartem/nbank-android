package helpers;

import static io.qameta.allure.Allure.step;
import models.CreateAccountResponse;
import models.GetAccountDetailsResponse;
import org.apache.http.HttpStatus;

import config.Config;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;

import java.util.Base64;
import java.util.List;

public class AccountApiHelper {

    private static String baseUri() {
        return Config.getProperty("backend.url");
    }

    private static String basicAuth(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    public static CreateAccountResponse createAccount(String username, String password) {
        return step("Create account via API as user " + username, () -> {
            CreateAccountResponse response = RestAssured.given()
                        .baseUri(baseUri())
                        .header("accept", "*/*")
                        .header("Authorization", basicAuth(username, password))
                        .log().ifValidationFails()
                        .when()
                        .post("/api/v1/accounts")
                        .then()
                        .log().ifValidationFails()
                        .statusCode(HttpStatus.SC_CREATED)
                        .extract()
                        .as(CreateAccountResponse.class);
            return response;
        });
    }

    public static List<GetAccountDetailsResponse> getCustomerAccounts(String username, String password) {
        return step("Get customer accounts", () -> {
            List<GetAccountDetailsResponse> list = RestAssured.given()
                        .baseUri(baseUri())
                        .header("accept", "*/*")
                        .header("Authorization", basicAuth(username, password))
                        .log().ifValidationFails()
                        .when()
                        .get("/api/v1/customer/accounts")
                        .then()
                        .log().ifValidationFails()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .as(new TypeRef<List<GetAccountDetailsResponse>>() {});
            return list;
        });
    }
}
