package api;

import config.ApiConfig;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.CourierLoginRequest;
import models.CourierRequest;
import models.OrderRequest;

import static io.restassured.RestAssured.given;

public class ApiClient {

    @Step("Creating courier with login {login} and password {password}")
    public Response createCourier(String login, String password, String firstName) {
        CourierRequest courier = new CourierRequest(login, password, firstName);
        return given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post(ApiConfig.BASE_URL + ApiConfig.COURIER_ENDPOINT);
    }

    @Step("Login courier with login {login}")
    public Response loginCourier(String login, String password) {
        CourierLoginRequest loginRequest = new CourierLoginRequest(login, password);
        return given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .post(ApiConfig.BASE_URL + ApiConfig.COURIER_LOGIN_ENDPOINT);
    }

    @Step("Deleting courier with id {courierId}")
    public Response deleteCourier(int courierId) {
        return given()
                .delete(ApiConfig.BASE_URL + ApiConfig.COURIER_ENDPOINT + "/" + courierId);
    }

    @Step("Creating order")
    public Response createOrder(OrderRequest order) {
        return given()
                .contentType(ContentType.JSON)
                .body(order)
                .post(ApiConfig.BASE_URL + ApiConfig.ORDER_ENDPOINT);
    }

    @Step("Getting orders list")
    public Response getOrdersList() {
        return given()
                .get(ApiConfig.BASE_URL + ApiConfig.ORDER_LIST_ENDPOINT);
    }
}