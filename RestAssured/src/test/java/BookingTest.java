import io.restassured.http.ContentType;
import net.datafaker.Faker;
import org.junit.jupiter.api.*;
import payloads.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingTest extends BaseTest {

    private static final Faker faker = new Faker();
    private static int bookingId;
    private static BookingPayload bookingPayload;

    @BeforeAll
    public static void setupData() {
        BookingDates dates = new BookingDates("2026-10-01", "2026-10-15");

        bookingPayload = new BookingPayload(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.number().numberBetween(100, 1000),
                faker.bool().bool(),
                dates,
                faker.food().dish()
        );
    }

    @Test
    @Order(1)
    @DisplayName("Deve criar uma reserva com sucesso (POST)")
    public void createBookingTest() {
        bookingId = given()
                .body(bookingPayload)
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .body("bookingid", isA(Integer.class))
                .body("booking.firstname", equalTo(bookingPayload.firstname()))
                .body("booking.lastname", equalTo(bookingPayload.lastname()))
                .extract()
                .path("bookingid");
    }

    @Test
    @Order(2)
    @DisplayName("Deve consultar a reserva criada e validar o Contrato (JSON Schema)")
    public void getBookingTest() {
        given()
                .when()
                .get("/booking/" + bookingId)
                .then()
                .statusCode(200)
                // Validação Estrutural (Contrato)
                .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"))
                // Validação de Negócio (Dados)
                .body("firstname", equalTo(bookingPayload.firstname()))
                .body("totalprice", equalTo(bookingPayload.totalprice()));
    }

    @Test
    @Order(3)
    @DisplayName("Deve atualizar a reserva completamente (PUT)")
    public void updateBookingTest() {
        BookingDates newDates = new BookingDates("2026-11-01", "2026-11-10");
        BookingPayload updatedPayload = new BookingPayload(
                faker.name().firstName(),
                faker.name().lastName(),
                1500,
                true,
                newDates,
                "Late Check-out"
        );

        given()
                .header("Cookie", "token=" + AuthManager.getToken())
                .body(updatedPayload)
                .when()
                .put("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .body("firstname", equalTo(updatedPayload.firstname()))
                .body("additionalneeds", equalTo("Late Check-out"));
    }

    @Test
    @Order(4)
    @DisplayName("Deve atualizar a reserva parcialmente (PATCH)")
    public void partialUpdateBookingTest() {
        Map<String, String> partialPayload = new HashMap<>();
        partialPayload.put("firstname", "QA");
        partialPayload.put("lastname", "Automation");

        given()
                .header("Cookie", "token=" + AuthManager.getToken())
                .body(partialPayload)
                .when()
                .patch("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("QA"))
                .body("lastname", equalTo("Automation"))
                .body("totalprice", equalTo(1500));
    }

    @Test
    @Order(5)
    @DisplayName("Deve excluir a reserva com sucesso (DELETE)")
    public void deleteBookingTest() {
        given()
                .header("Cookie", "token=" + AuthManager.getToken())
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .statusCode(201);
    }

    @Test
    @Order(6)
    @DisplayName("Deve listar todos os IDs de reservas (GET /booking)")
    public void getAllBookingIdsTest() {
        given()
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class))
                .body("[0]", hasKey("bookingid"));
    }

    @Test
    @Order(7)
    @DisplayName("Deve listar IDs filtrando por checkin e checkout (GET /booking?checkin=...&checkout=...)")
    public void getBookingIdsByDateTest() {
        given()
                .queryParam("checkin", "2026-10-01")
                .queryParam("checkout", "2026-10-15")
                .when()
                .get("/booking")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));
    }

    @Test
    @Order(8)
    @DisplayName("Deve validar o Health Check da API (GET /ping)")
    public void healthCheckTest() {
        given()
                .when()
                .get("/ping")
                .then()
                .statusCode(201) // Regra de negócio específica do Restful-Booker
                .body(containsString("Created"));
    }

    @Test
    @Order(9)
    @DisplayName("Deve falhar ao buscar uma reserva inexistente (GET /booking/999999)")
    public void getBookingNotFoundTest() {
        given()
                .when()
                .get("/booking/999999")
                .then()
                .statusCode(404)
                .body(equalTo("Not Found"));
    }

    @Test
    @Order(10)
    @DisplayName("Deve falhar ao criar reserva com payload inválido/incompleto (POST /booking)")
    public void createBookingInvalidPayloadTest() {
        // Envio de um Map vazio simulando a ausência dos campos obrigatórios
        Map<String, String> invalidPayload = new HashMap<>();

        given()
                .body(invalidPayload)
                .when()
                .post("/booking")
                .then()
                .statusCode(500)
                .body(containsString("Internal Server Error"));
    }

    @Test
    @Order(11)
    @DisplayName("Deve barrar atualização de reserva sem token de autorização (PUT /booking/{id})")
    public void updateBookingUnauthorizedTest() {
        given()
                // Omissão proposital do header "Cookie" com o token
                .body(bookingPayload)
                .when()
                .put("/booking/" + bookingId) // Utiliza o ID gerado no @Order(1) antes de ser deletado, ou recrie um
                .then()
                .statusCode(403)
                .body(equalTo("Forbidden"));
    }

    @Test
    @Order(12)
    @DisplayName("Deve barrar atualização de reserva utilizando ID inexistente (PUT /booking/999999)")
    public void updateBookingMethodNotAllowedTest() {
        given()
                .header("Cookie", "token=" + AuthManager.getToken())
                .body(bookingPayload)
                .when()
                .put("/booking/999999")
                .then()
                .statusCode(405)
                .body(equalTo("Method Not Allowed"));
    }

    @Test
    @Order(13)
    @DisplayName("Deve barrar exclusão de reserva sem token de autorização (DELETE /booking/{id})")
    public void deleteBookingUnauthorizedTest() {
        given()
                .when()
                .delete("/booking/1") // Tentativa de deletar ID aleatório sem auth
                .then()
                .statusCode(403)
                .body(equalTo("Forbidden"));
    }
}