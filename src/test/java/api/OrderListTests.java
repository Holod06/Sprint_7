package api;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@Epic("REST API Tests")
@Feature("Orders List")
@DisplayName("Tests for orders list endpoint")
public class OrderListTests {

    private ApiClient apiClient;

    public OrderListTests() {
        this.apiClient = new ApiClient();
    }

    @Test
    @Story("Getting orders list")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Getting orders list returns an array")
    public void orderListShouldReturnArrayOfOrders() {
        Response response = apiClient.getOrdersList();

        response.then()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders", isA(java.util.List.class));
    }

    @Test
    @Story("Orders list contains data")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Response contains orders field")
    public void orderListResponseShouldContainOrdersField() {
        Response response = apiClient.getOrdersList();

        response.then()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Test
    @Story("Orders list has correct structure")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Each order contains track field")
    public void eachOrderShouldContainTrackField() {
        Response response = apiClient.getOrdersList();

        response.then()
                .statusCode(200)
                .body("orders[0].track", notNullValue());
    }
}