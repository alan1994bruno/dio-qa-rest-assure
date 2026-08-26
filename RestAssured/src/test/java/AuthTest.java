import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import payloads.AuthPayload;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasKey;

public class AuthTest extends BaseTest {

    @Test
    @DisplayName("Deve negar autenticação com credenciais incorretas (POST /auth)")
    public void authFailTest() {
        AuthPayload invalidAuth = new AuthPayload("adminErrado", "senhaErrada123");

        given()
                .body(invalidAuth)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .body("reason", equalTo("Bad credentials"))
                .body("$", not(hasKey("token")));
    }
}