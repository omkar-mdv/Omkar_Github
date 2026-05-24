package base;

import org.testng.annotations.BeforeClass;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.io.FileInputStream;
import java.util.Properties;

public class ApiBaseTest {

    protected RequestSpecification requestSpec;
    protected static Properties config;

    @BeforeClass
    public void setup() {

        // ✅ Load config file
        config = new Properties();
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config/config.properties");
            config.load(fis);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config file", e);
        }

        // ✅ Request Spec
        requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://kb.remit.in")
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .build();
    }
}