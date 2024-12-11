package stubs.com.ms.igse.authentication;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class StubServerConfig {
    @Bean
    public WireMockServer core(){
        System.out.println("Igse-Core Stub starting ...");
        return createServer(9701,"src/test/resources/stubdata/locations/core");
    }

    @Bean
    public WireMockServer payment(){
        return createServer(9702,"src/test/resources/stubdata/locations/payment");
    }

    public WireMockServer createServer(int port, String fileMapping){

        return new WireMockServer(WireMockConfiguration.options()
                .jettyStopTimeout(100L)
                .bindAddress("localhost")
                .port(port)
                .usingFilesUnderClasspath(fileMapping)
                .extensions(new ResponseTemplateTransformer(true))
                .notifier(new Slf4jNotifier(true))
        );

    }
}
