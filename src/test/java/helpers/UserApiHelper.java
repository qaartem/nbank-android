package helpers;

import static io.qameta.allure.Allure.step;
import models.CreateUserRequest;
import org.apache.http.HttpStatus;

import config.Config;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class UserApiHelper {

    private static String baseUri() {
        return Config.getProperty("backend.url");
    }

    private static String adminAuth() {
        return "Basic " + Config.getProperty("admin.token");
    }

    public static CreateUserRequest createUser(CreateUserRequest userRequest) {
        return step("Admin creates user " + userRequest.getUsername(), () -> {
            RestAssured.given()
                    .baseUri(baseUri())
                    .header("accept", "*/*")
                    .header("Authorization", adminAuth())
                    .contentType(ContentType.JSON)
                    .body(userRequest)
                    .log().ifValidationFails()
                    .when()
                    .post("/api/v1/admin/users")
                    .then()
                    .log().ifValidationFails()
                    .statusCode(HttpStatus.SC_CREATED);
            return userRequest;
        });
    }
}
