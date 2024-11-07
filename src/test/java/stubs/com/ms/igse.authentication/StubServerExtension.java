package stubs.com.ms.igse.authentication;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.function.Consumer;

public class StubServerExtension implements BeforeEachCallback, AfterEachCallback, AfterAllCallback {
    private final String[] stubServerNames;

    public StubServerExtension() {
        stubServerNames = null;
    }

    public StubServerExtension(String... stubServerNames) {
        this.stubServerNames = stubServerNames;
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        stubServerAction(context, WireMockServer::stop);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
          stubServerAction(context, WireMockServer::stop);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        stubServerAction(context, WireMockServer::resetAll);
        stubServerAction(context, WireMockServer::start);
    }

    private void stubServerAction(ExtensionContext context, Consumer<WireMockServer> consumer) {
        ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
        if (stubServerNames == null) {
            applicationContext.getBeansOfType(WireMockServer.class).forEach((s, wiremockServer) -> {
                consumer.accept(wiremockServer);
            });
        } else {
            Arrays.stream(stubServerNames).forEach(s -> {
                WireMockServer wireMockServer = applicationContext.getBean(s, WireMockServer.class);
                consumer.accept(wireMockServer);

            });
        }
    }
}
