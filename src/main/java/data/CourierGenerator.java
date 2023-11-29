package data;

import org.apache.commons.lang3.RandomStringUtils;

public class CourierGenerator {
    public static CourierData getRandomCourier() {
        String login = RandomStringUtils.randomAlphabetic(8);
        String password = RandomStringUtils.randomAlphabetic(8);
        String firstName = RandomStringUtils.randomAlphabetic(8);

        return new CourierData(login, password, firstName);
    }
}
