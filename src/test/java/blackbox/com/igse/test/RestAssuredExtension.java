package blackbox.com.igse.test;

import io.restassured.RestAssured;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

public class RestAssuredExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        System.out.println("RestAssuredExtension Start");
        ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
        Environment environment = applicationContext.getEnvironment();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = Integer.parseInt(Objects.requireNonNull(environment.getProperty("server.port")));
        RestAssured.basePath = environment.getProperty("server.servlet.context.path");
        System.out.println("RestAssuredExtension End");
    }
}
