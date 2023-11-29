package courier_test;

import client.CourierClient;
import data.CourierCredentials;
import data.CourierData;
import data.CourierGenerator;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginCourierTest {
    CourierClient courierClient;
    CourierData courier;
    Integer courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = CourierGenerator.getRandomCourier();
        ValidatableResponse createResponse = courierClient.createCourier(courier);
        if (createResponse.extract().statusCode() == HttpStatus.SC_OK) {
            courierId = createResponse.extract().path("id");
        }
    }

    @After
    public void cleanUp() {
        if (courierId != null) {
            courierClient.deleteCourier(courierId);
        }
    }

    @Test
    public void successfulLoginReturnsOkAndId() {
        CourierCredentials validCredentials = CourierCredentials.from(courier);
        courierClient.loginCourier(validCredentials).assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", notNullValue());
    }

    @Test
    public void loginWithMissingLoginReturnsScBadRequest() {
        CourierCredentials credentialsWithoutLogin = new CourierCredentials("", "validPassword");
        courierClient.loginCourier(credentialsWithoutLogin).assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для входа"));
    }

    @Test
    public void loginWithMissingPasswordReturnsSCScBadRequest() {
        CourierCredentials credentialsWithoutPassword = new CourierCredentials("validLogin", "");
        courierClient.loginCourier(credentialsWithoutPassword).assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для входа"));
    }

    @Test
    public void loginWithInvalidCredentialsReturnsScNotFound() {
        CourierCredentials invalidCredentials = new CourierCredentials("invalidLogin", "invalidPassword");
        courierClient.loginCourier(invalidCredentials).assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", is("Учетная запись не найдена"));
    }
}
