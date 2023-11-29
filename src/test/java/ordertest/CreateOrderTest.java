package order_test;

import client.OrderClient;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class CreateOrderTest {
    private OrderClient orderClient;

    @Before
    public void setUp() {
        this.orderClient = new OrderClient();
    }

    @Test
    public void testGetOrdersReturnsListOfOrders() {
        ValidatableResponse response = orderClient.getOrders();
        response.assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("orders", is(notNullValue()))
                .body("orders", hasSize(greaterThan(0)));
    }

    @Test
    public void testGetOrdersWithNonExistentCourierId() {
        Map<String, Object> params = new HashMap<>();
        params.put("courierId", 0);

        ValidatableResponse response = orderClient.getOrdersWithParams(params);
        response.assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", is("Курьер с идентификатором 0 не найден"));
    }


}
