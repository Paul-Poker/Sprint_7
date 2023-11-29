package couriertest;

import client.CourierClient;
import data.CourierCredentials;
import data.CourierData;
import data.CourierGenerator;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateCourierTest {
    private CourierClient courierClient;
    private CourierData courier;
    private Integer courierId;

    @Before
    public void setup() {
        courierClient = new CourierClient();
        courier = CourierGenerator.getRandomCourier();
    }

    @After
    public void cleanUp() {
        if (courierId != null) {
            courierClient.deleteCourier(courierId);
        }

    }

    @Test
    @DisplayName("Создание и логин курьера")
    public void courierCanBeCreatedAndLoggedInTest() {
        ValidatableResponse createResponse = courierClient.createCourier(courier);
        createResponse.assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("ok", is(true));

        courierId = createResponse.extract().path("id");

        CourierCredentials credentials = CourierCredentials.from(courier);
        ValidatableResponse responseLogin = courierClient.loginCourier(credentials);
        responseLogin.assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Создание уже имеющегося курьера")
    public void cannotCreateDuplicateCourier() {

        ValidatableResponse createResponse = courierClient.createCourier(courier);
        createResponse.assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("ok", is(true));


        ValidatableResponse duplicateCreateResponse = courierClient.createCourier(courier);
        duplicateCreateResponse.assertThat()
                .statusCode(HttpStatus.SC_CONFLICT)
                .body("message", is("Этот логин уже используется. Попробуйте другой.")); //в Swagger указан другой текст ошибки :"Этот логин уже используется."
    }

    @Test
    @DisplayName("Создание курьера без логина или пароля")
    public void cannotCreateCourierWithoutLoginOrPassword() {

        CourierData courierWithoutLogin = new CourierData(null, "password", "Name");

        ValidatableResponse responseWithoutLogin = courierClient.createCourier(courierWithoutLogin);
        responseWithoutLogin.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для создания учетной записи"));

        CourierData courierWithoutPassword = new CourierData("login", null, "Name");

        ValidatableResponse responseWithoutPassword = courierClient.createCourier(courierWithoutPassword);
        responseWithoutPassword.assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }


}
