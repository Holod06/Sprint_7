package api;

import io.qameta.allure.*;
import models.OrderRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

@Epic("REST API Tests")
@Feature("Order Creation")
@DisplayName("Tests for order creation endpoint")
public class OrderTests {

    private ApiClient apiClient;

    public OrderTests() {
        this.apiClient = new ApiClient();
    }

    @Test
    @Story("Order creation with BLACK color")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Order with BLACK color")
    public void orderShouldBeCreatedWithBlackColor() {
        OrderRequest order = createOrderWithColor(Arrays.asList("BLACK"));

        Response response = apiClient.createOrder(order);

        response.then()
                .statusCode(201)
                .body("track", notNullValue())
                .body("track", isA(Integer.class));
    }

    @Test
    @Story("Order creation with GREY color")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Order with GREY color")
    public void orderShouldBeCreatedWithGreyColor() {
        OrderRequest order = createOrderWithColor(Arrays.asList("GREY"));

        Response response = apiClient.createOrder(order);

        response.then()
                .statusCode(201)
                .body("track", notNullValue())
                .body("track", isA(Integer.class));
    }

    @Test
    @Story("Order creation with both colors")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Order with both BLACK and GREY colors")
    public void orderShouldBeCreatedWithBothColors() {
        OrderRequest order = createOrderWithColor(Arrays.asList("BLACK", "GREY"));

        Response response = apiClient.createOrder(order);

        response.then()
                .statusCode(201)
                .body("track", notNullValue())
                .body("track", isA(Integer.class));
    }

    @Test
    @Story("Order creation without color")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Order without color specified")
    public void orderShouldBeCreatedWithoutColor() {
        OrderRequest order = createOrderWithColor(new ArrayList<>());

        Response response = apiClient.createOrder(order);

        response.then()
                .statusCode(201)
                .body("track", notNullValue())
                .body("track", isA(Integer.class));
    }

    @Test
    @Story("Order creation with null color")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Order with null value for color")
    public void orderShouldBeCreatedWithNullColor() {
        OrderRequest order = createOrderWithColor(null);

        Response response = apiClient.createOrder(order);

        response.then()
                .statusCode(201)
                .body("track", notNullValue())
                .body("track", isA(Integer.class));
    }

    private OrderRequest createOrderWithColor(List<String> colors) {
        OrderRequest order = new OrderRequest();
        order.setFirstName("Ivan");
        order.setLastName("Petrov");
        order.setAddress("Saint Petersburg");
        order.setMetroStation(1);
        order.setPhone("+7 911 900 80 90");
        order.setRentTime(5);
        order.setDeliveryDate("2024-12-31");
        order.setComment("Test order");
        order.setColor(colors);

        return order;
    }
}