package com.igse.repository;

import static com.igse.common.IgseConstants.BEARER;
import static com.igse.common.IgseConstants.CORRELATION_ID;
import com.igse.dto.WalletInfoDTO;
import com.igse.exception.UserException;
import com.igse.repository.core.CoreError;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.util.retry.Retry;
import java.time.Duration;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRepo {
    private final WebClient webClient;
    private final CoreError coreError;

    @Value("${infrastructure.services.igse_payment.endpoint}")
    private String basePath;

    @Value("${infrastructure.services.igse_payment.walletDetailPath}")
    private String walletDetailPath;

    @CircuitBreaker(name = "wallet", fallbackMethod = "walletNotFound")
    public WalletInfoDTO walletDetails(String customerId, String token, String correlationId) {
        try {
            return webClient.get()
                    .uri(basePath + walletDetailPath, customerId)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                    .header("X-Correlation-Id", UUID.randomUUID().toString())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, coreError::handleCoreError)
                    .bodyToMono(WalletInfoDTO.class)
                    .retryWhen(retryWallet(correlationId))
                    .block();
        } catch (WebClientRequestException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private Retry retryWallet(String correlationId) {
        return Retry
                .fixedDelay(3, Duration.ofMillis(500))
                .doAfterRetry(x -> {
                            MDC.put(CORRELATION_ID, correlationId);
                            log.info("Path {} Total Retry {}", basePath + walletDetailPath, x.totalRetries());
                        }
                )
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                    throw UserException.builder().status(500).message(HttpStatus.INTERNAL_SERVER_ERROR.toString()).build();
                });

    }

    public WalletInfoDTO walletNotFound(Throwable e) {
        log.error("wallet {}", e.getMessage());
        return new WalletInfoDTO();
    }
}
