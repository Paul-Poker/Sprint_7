package client;

import data.OrderData;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestClient {
    private static final String ORDER_PATH = "/api/v1/orders";

    @Step("Создание заказа")
    public ValidatableResponse createOrder(OrderData orderData) {
        return given()
                .spec(requestSpecification())
                .body(orderData)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Получение списка заказов")
    public ValidatableResponse getOrders() {
        return given()
                .spec(requestSpecification())
                .when()
                .get(ORDER_PATH)
                .then();
    }

    @Step("Получение списка заказов с параметрами")
    public ValidatableResponse getOrdersWithParams(Map<String, Object> params) {
        return given()
                .spec(requestSpecification())
                .queryParams(params)
                .when()
                .get(ORDER_PATH)
                .then();
    }
}