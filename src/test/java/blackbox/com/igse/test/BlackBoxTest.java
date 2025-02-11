package blackbox.com.igse.test;

import com.igse.Application;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import stubs.com.ms.igse.authentication.StubServerConfig;
import stubs.com.ms.igse.authentication.StubServerExtension;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({SpringExtension.class, StubServerExtension.class})
@ActiveProfiles("test")
@SpringBootTest(classes = {Application.class, RestAssuredExtension.class, StubServerConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@Disabled
public @interface BlackBoxTest {
}
