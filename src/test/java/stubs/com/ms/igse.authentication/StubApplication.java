package stubs.com.ms.igse.authentication;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import java.security.Security;

@Configuration
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class StubApplication {
    private final StubServerRunner stubServerRunner;

    public StubApplication(StubServerRunner stubServerRunner) {
        this.stubServerRunner = stubServerRunner;
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "test");
        System.setProperty("server.port", "8091");
        SpringApplication.run(StubApplication.class, args);
    }

    @PostConstruct
    public void init() {
        stubServerRunner.start();
    }

    @PreDestroy
    void tearDown() {
        stubServerRunner.shutDown();
    }
}
