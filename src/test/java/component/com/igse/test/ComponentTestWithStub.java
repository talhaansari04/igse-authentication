package component.com.igse.test;

import com.igse.Application;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import stubs.com.ms.igse.authentication.StubServerConfig;
import stubs.com.ms.igse.authentication.StubServerExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({SpringExtension.class, StubServerExtension.class})
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Import({StubServerConfig.class})
public @interface ComponentTestWithStub {
}
