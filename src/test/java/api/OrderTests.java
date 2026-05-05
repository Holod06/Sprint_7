package api;

import io.qameta.allure.*;
import models.OrderRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.*;

@Epic("REST API Tests")
@Feature("Order Creation")
@DisplayName("Tests for order creation endpoint")
public class OrderTests {

    private ApiClient apiClient;

    public OrderTests() {
        this.apiClient = new ApiClient();
    }

    @ParameterizedTest
    @MethodSource("provideColors")
    @Story("Order creation with different colors")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Order is created with color: {1}")
    public void orderShouldBeCreatedWithDifferentColors(List<String> colors, String description) {
        OrderRequest order = createOrderWithColor(colors);

        Response response = apiClient.createOrder(order);

        response.then()
                .statusCode(SC_CREATED)
                .body("track", notNullValue())
                .body("track", isA(Integer.class));
    }

    private OrderRequest createOrderWithColor(List<String> colors) {
        return new OrderRequest(
                "Ivan",
                "Petrov",
                "Saint Petersburg",
                1,
                "+7 911 900 80 90",
                5,
                "2024-12-31",
                "Test order",
                colors
        );
    }

    private static Stream<Object[]> provideColors() {
        return Stream.of(
                new Object[]{Arrays.asList("BLACK"), "BLACK"},
                new Object[]{Arrays.asList("GREY"), "GREY"},
                new Object[]{Arrays.asList("BLACK", "GREY"), "BLACK and GREY"},
                new Object[]{new ArrayList<>(), "No color"},
                new Object[]{null, "Null color"}
        );
    }
}