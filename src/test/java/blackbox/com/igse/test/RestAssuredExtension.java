package blackbox.com.igse.test;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Objects;

@Slf4j
public class RestAssuredExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        log.info("RestAssuredExtension Start");
        ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
        Environment environment = applicationContext.getEnvironment();

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = Integer.parseInt(Objects.requireNonNull(environment.getProperty("server.port")));
        RestAssured.basePath = environment.getProperty("server.servlet.context.path");
        log.info("RestAssuredExtension End");
    }
}
