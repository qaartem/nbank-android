package helpers;

import static io.qameta.allure.Allure.step;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import config.Config;
import io.restassured.RestAssured;

public class ApiHelper {
    private static final Logger log = LoggerFactory.getLogger(ApiHelper.class);

    public static void healthCheck() {
        log.info("Running API health check: GET /api/v1/admin/users");
        step("Check API health before start testing", () -> {
            RestAssured.given()
                    .baseUri(Config.getProperty("backend.url"))
                    .header("accept", "*/*")
                    .header("Authorization", "Basic " + Config.getProperty("admin.token"))
                    .when()
                    .get("/api/v1/admin/users")
                    .then()
                    .log().ifValidationFails()
                    .assertThat().statusCode(HttpStatus.SC_OK);
            log.info("API health check passed");
        });
    }
}