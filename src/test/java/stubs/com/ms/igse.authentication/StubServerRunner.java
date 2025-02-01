package stubs.com.ms.igse.authentication;

import com.github.tomakehurst.wiremock.WireMockServer;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
public class StubServerRunner {
    private final List<WireMockServer> wireMockServers;

    public StubServerRunner(List<WireMockServer> wireMockServers) {
        this.wireMockServers = wireMockServers;

    }

    @PostConstruct
    public void announce() {
        log.info("StubServerRunner stubs {}", wireMockServers.size());
    }

    public void start() {
        wireMockServers.forEach(WireMockServer::start);
    }

    public void shutDown() {
        wireMockServers.forEach(WireMockServer::resetAll);
        wireMockServers.forEach(WireMockServer::stop);
    }
}
