package api;

import config.ApiConfig;
import io.qameta.allure.*;
import io.restassured.response.Response;
import models.CourierLoginRequest;
import models.CourierRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

@Epic("REST API Tests")
@Feature("Courier Login")
@DisplayName("Tests for courier login endpoint")
public class CourierLoginTests {

    private ApiClient apiClient;
    private int courierId = -1;
    private String testLogin;
    private String testPassword;

    @BeforeEach
    public void setUp() {
        apiClient = new ApiClient();
        testLogin = "logintest_" + System.currentTimeMillis();
        testPassword = "password123";

        // Create courier before each test
        CourierRequest courier = new CourierRequest(testLogin, testPassword, "Test");
        Response createResponse = apiClient.createCourier(courier);
        if (createResponse.statusCode() == SC_CREATED) {
            Response loginResponse = apiClient.loginCourier(new CourierLoginRequest(testLogin, testPassword));
            if (loginResponse.statusCode() == SC_OK) {
                courierId = loginResponse.jsonPath().getInt("id");
            }
        }
    }

    @AfterEach
    public void tearDown() {
        if (courierId != -1) {
            apiClient.deleteCourier(courierId);
        }
    }

    @Test
    @Story("Successful courier login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Courier successfully logs in and receives id")
    public void courierShouldLoginSuccessfully() {
        Response loginResponse = apiClient.loginCourier(new CourierLoginRequest(testLogin, testPassword));

        loginResponse.then()
                .statusCode(SC_OK)
                .body("id", notNullValue())
                .body("id", isA(Integer.class));
    }

    @Test
    @Story("Login without login field")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Error when login is missing")
    public void shouldReturnErrorWhenLoginIsMissing() {
        Response response = given()
                .contentType("application/json")
                .body(new CourierLoginRequest(null, "password123"))
                .post(ApiConfig.BASE_URL + ApiConfig.COURIER_LOGIN_ENDPOINT);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Story("Login with wrong password")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Error with wrong password")
    public void shouldReturnErrorWithWrongPassword() {
        Response loginResponse = apiClient.loginCourier(new CourierLoginRequest(testLogin, "wrongpassword"));

        loginResponse.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Story("Login with non-existent login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Error when logging in as non-existent user")
    public void shouldReturnErrorForNonExistentCourier() {
        String fakeLogin = "completely_fake_login_" + System.currentTimeMillis();

        Response response = apiClient.loginCourier(new CourierLoginRequest(fakeLogin, "password"));

        response.then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}