package api;

import config.ApiConfig;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("REST API Tests")
@Feature("Courier Creation")
@DisplayName("Tests for courier creation endpoint")
public class CourierTests {

    private ApiClient apiClient;

    public CourierTests() {
        this.apiClient = new ApiClient();
    }

    @Test
    @Story("Successful courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Courier is created successfully with correct data")
    public void courierShouldBeCreatedSuccessfully() {
        String testLogin = "courier_" + System.currentTimeMillis();

        Response createResponse = apiClient.createCourier(testLogin, "password123", "John");

        createResponse.then()
                .statusCode(201)
                .body("ok", equalTo(true));

        // Get courier ID and cleanup
        Response loginResponse = apiClient.loginCourier(testLogin, "password123");
        int courierId = loginResponse.jsonPath().getInt("id");
        apiClient.deleteCourier(courierId);
    }

    @Test
    @Story("Duplicate courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Cannot create two identical couriers")
    public void shouldNotCreateDuplicateCourier() {
        String testLogin = "duptest_" + System.currentTimeMillis();

        // Create first courier
        Response firstResponse = apiClient.createCourier(testLogin, "password123", "John");
        firstResponse.then().statusCode(201);

        // Try to create duplicate
        Response secondResponse = apiClient.createCourier(testLogin, "password123", "Jane");
        secondResponse.then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

        // Cleanup
        Response loginResponse = apiClient.loginCourier(testLogin, "password123");
        int courierId = loginResponse.jsonPath().getInt("id");
        apiClient.deleteCourier(courierId);
    }

    @Test
    @Story("Courier creation without login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Error when login is missing")
    public void shouldReturnErrorWhenLoginIsMissing() {
        Response response = given()
                .contentType("application/json")
                .body("{\"password\": \"password123\", \"firstName\": \"John\"}")
                .post(ApiConfig.BASE_URL + ApiConfig.COURIER_ENDPOINT);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Story("Courier creation without password")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Error when password is missing")
    public void shouldReturnErrorWhenPasswordIsMissing() {
        String testLogin = "nopwd_" + System.currentTimeMillis();

        Response response = given()
                .contentType("application/json")
                .body("{\"login\": \"" + testLogin + "\", \"firstName\": \"John\"}")
                .post(ApiConfig.BASE_URL + ApiConfig.COURIER_ENDPOINT);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Story("Courier creation with minimal data")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Courier is created with mandatory fields only (login and password)")
    public void courierShouldBeCreatedWithMinimalData() {
        String testLogin = "minimal_" + System.currentTimeMillis();

        Response createResponse = apiClient.createCourier(testLogin, "password123", null);

        createResponse.then()
                .statusCode(201)
                .body("ok", equalTo(true));

        // Get courier ID and cleanup
        Response loginResponse = apiClient.loginCourier(testLogin, "password123");
        int courierId = loginResponse.jsonPath().getInt("id");
        apiClient.deleteCourier(courierId);
    }
}