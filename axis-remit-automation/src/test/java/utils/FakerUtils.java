package utils;

import com.github.javafaker.Faker;

public class FakerUtils {

    static Faker faker = new Faker();

    public static String getName() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        return firstName + " " + lastName;
    }

    public static String getEmail() {
        String randomPrefix = faker.name().firstName() + faker.number().digits(4);
        return randomPrefix.toLowerCase() + "@yopmail.com";
    }

    public static String getMobile() {
        return faker.number().digits(10);
    }

    public static String getPassword() {
        return "Test@" + faker.number().digits(6);
    }
}