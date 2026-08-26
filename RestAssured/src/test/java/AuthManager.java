import io.restassured.http.ContentType;
import payloads.AuthPayload;
import utils.ConfigReader;

import static io.restassured.RestAssured.given;

public class AuthManager {
    public static String getToken() {
        AuthPayload authPayload = new AuthPayload(ConfigReader.getProperty("api.auth.username"), ConfigReader.getProperty("api.auth.password"));

        return given()
                .body(authPayload)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}