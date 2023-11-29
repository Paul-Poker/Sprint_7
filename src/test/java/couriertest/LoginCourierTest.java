package couriertest;

import client.CourierClient;
import data.CourierCredentials;
import data.CourierData;
import data.CourierGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginCourierTest {
    private CourierClient courierClient;
    private CourierData courier;
    private Integer courierId;

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
    @DisplayName("Авторизация существующего пользователя")
    public void successfulLoginReturnsOkAndId() {
        CourierCredentials validCredentials = CourierCredentials.from(courier);
        courierClient.loginCourier(validCredentials).assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Авторизация c пустым логином")
    public void loginWithMissingLoginReturnsScBadRequest() {
        CourierCredentials credentialsWithoutLogin = new CourierCredentials("", "validPassword");
        courierClient.loginCourier(credentialsWithoutLogin).assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация c пустым паролем")
    public void loginWithMissingPasswordReturnsSCScBadRequest() {
        CourierCredentials credentialsWithoutPassword = new CourierCredentials("validLogin", "");
        courierClient.loginCourier(credentialsWithoutPassword).assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация под несуществующим пользователем")
    public void loginWithInvalidCredentialsReturnsScNotFound() {
        CourierCredentials invalidCredentials = new CourierCredentials("invalidLogin", "invalidPassword");
        courierClient.loginCourier(invalidCredentials).assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", is("Учетная запись не найдена"));
    }
}
