package api;

import config.ApiConfig;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("REST API Tests")
@Feature("Courier Login")
@DisplayName("Tests for courier login endpoint")
public class CourierLoginTests {

    private ApiClient apiClient;

    public CourierLoginTests() {
        this.apiClient = new ApiClient();
    }

    @Test
    @Story("Successful courier login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Courier successfully logs in and receives id")
    public void courierShouldLoginSuccessfully() {
        String testLogin = "logintest_" + System.currentTimeMillis();
        String testPassword = "password123";

        // Create courier
        Response createResponse = apiClient.createCourier(testLogin, testPassword, "Test");
        createResponse.then().statusCode(201);

        // Login
        Response loginResponse = apiClient.loginCourier(testLogin, testPassword);
        int courierId = loginResponse.jsonPath().getInt("id");

        loginResponse.then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("id", isA(Integer.class));

        // Cleanup
        apiClient.deleteCourier(courierId);
    }

    @Test
    @Story("Login without login field")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Error when login is missing")
    public void shouldReturnErrorWhenLoginIsMissing() {
        Response response = given()
                .contentType("application/json")
                .body("{\"password\": \"password123\"}")
                .post(ApiConfig.BASE_URL + ApiConfig.COURIER_LOGIN_ENDPOINT);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Story("Login with wrong password")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Error with wrong password")
    public void shouldReturnErrorWithWrongPassword() {
        String testLogin = "wrongpwd_" + System.currentTimeMillis();
        String testPassword = "password123";

        // Create courier
        Response createResponse = apiClient.createCourier(testLogin, testPassword, "Test");
        createResponse.then().statusCode(201);

        // Try to login with wrong password
        Response loginResponse = apiClient.loginCourier(testLogin, "wrongpassword");

        loginResponse.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));

        // Get correct ID and cleanup
        Response correctLoginResponse = apiClient.loginCourier(testLogin, testPassword);
        int courierId = correctLoginResponse.jsonPath().getInt("id");
        apiClient.deleteCourier(courierId);
    }

    @Test
    @Story("Login with non-existent login")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Error when logging in as non-existent user")
    public void shouldReturnErrorForNonExistentCourier() {
        String fakeLogin = "completely_fake_login_" + System.currentTimeMillis();

        Response response = apiClient.loginCourier(fakeLogin, "password");

        response.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}