package utils;

import com.github.javafaker.Faker;

public class FakerUtils {

    static Faker faker = new Faker();

    public static String getName() {
        return faker.name().firstName() + " " + faker.name().lastName();
    }

    public static String getEmail() {
        String randomPrefix = faker.name().firstName() + faker.number().digits(4);
        return randomPrefix.toLowerCase() + "@yopmail.com";
    }

    /**
     * Generates mobile number with given digit length
     */
    public static String getMobile(int digits) {
        return faker.number().digits(digits);
    }

    public static String getPassword() {
        return "Test@" + faker.number().digits(6);
    }
}