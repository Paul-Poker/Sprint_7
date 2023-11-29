package order_test;

import client.OrderClient;
import data.OrderData;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderParameterizedTest {
    OrderClient orderClient;
    List<String> color;

    public CreateOrderParameterizedTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {List.of()}
        });
    }

    @Before
    public void setUp() {
        this.orderClient = new OrderClient();
    }

    @Test
    public void testCreateOrderWithColor() {
        OrderData orderData = new OrderData("Naruto", "Uchiha", "Konoha, 142 apt.",
                4, "+7 800 355 35 35", 5, "2020-06-06",
                "Saske, come back to Konoha", this.color);
        ValidatableResponse response = orderClient.createOrder(orderData);
        response.assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("track", notNullValue());
    }
}