package base;

import org.testng.annotations.BeforeClass;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ApiBaseTest {

    protected RequestSpecification requestSpec;

    @BeforeClass
    public void setup() {

        requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://kb.remit.in")
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .build();
    }
}