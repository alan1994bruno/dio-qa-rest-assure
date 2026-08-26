import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import utils.ConfigReader;

public class BaseTest {

    @BeforeAll
    public static void setupGlobal() {
        RestAssured.baseURI = ConfigReader.getProperty("api.base.uri");;

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", "PostmanRuntime/7.49.1")
                .build();

        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }
}