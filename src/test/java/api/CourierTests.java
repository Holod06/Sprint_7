package api;

import config.ApiConfig;
import io.qameta.allure.*;
import models.CourierRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

@Epic("REST API Tests")
@Feature("Courier Creation")
@DisplayName("Tests for courier creation endpoint")
public class CourierTests {

    private ApiClient apiClient;
    private int courierId = -1;
    private String testLogin;

    @BeforeEach
    public void setUp() {
        apiClient = new ApiClient();
        testLogin = "courier_" + System.currentTimeMillis();
    }

    @AfterEach
    public void tearDown() {
        if (courierId != -1) {
            apiClient.deleteCourier(courierId);
        }
    }

    @Test
    @Story("Successful courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Courier is created successfully with correct data")
    public void courierShouldBeCreatedSuccessfully() {
        CourierRequest courier = new CourierRequest(testLogin, "password123", "John");

        Response createResponse = apiClient.createCourier(courier);

        createResponse.then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        // Get courier ID
        Response loginResponse = apiClient.loginCourier(new models.CourierLoginRequest(testLogin, "password123"));
        courierId = loginResponse.jsonPath().getInt("id");
    }

    @Test
    @Story("Duplicate courier creation")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Cannot create two identical couriers")
    public void shouldNotCreateDuplicateCourier() {
        CourierRequest courier = new CourierRequest(testLogin, "password123", "John");

        // Create first courier
        Response firstResponse = apiClient.createCourier(courier);
        firstResponse.then().statusCode(SC_CREATED);

        // Try to create duplicate
        Response secondResponse = apiClient.createCourier(courier);
        secondResponse.then()
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

        // Get ID for cleanup
        Response loginResponse = apiClient.loginCourier(new models.CourierLoginRequest(testLogin, "password123"));
        courierId = loginResponse.jsonPath().getInt("id");
    }

    @Test
    @Story("Courier creation without login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Error when login is missing")
    public void shouldReturnErrorWhenLoginIsMissing() {
        Response response = given()
                .contentType("application/json")
                .body(new CourierRequest(null, "password123", "John"))
                .post(ApiConfig.BASE_URL + ApiConfig.COURIER_ENDPOINT);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Story("Courier creation without password")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Error when password is missing")
    public void shouldReturnErrorWhenPasswordIsMissing() {
        Response response = given()
                .contentType("application/json")
                .body(new CourierRequest(testLogin, null, "John"))
                .post(ApiConfig.BASE_URL + ApiConfig.COURIER_ENDPOINT);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Story("Courier creation with minimal data")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Courier is created with mandatory fields only (login and password)")
    public void courierShouldBeCreatedWithMinimalData() {
        CourierRequest courier = new CourierRequest(testLogin, "password123", null);

        Response createResponse = apiClient.createCourier(courier);

        createResponse.then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        // Get ID for cleanup
        Response loginResponse = apiClient.loginCourier(new models.CourierLoginRequest(testLogin, "password123"));
        courierId = loginResponse.jsonPath().getInt("id");
    }
}