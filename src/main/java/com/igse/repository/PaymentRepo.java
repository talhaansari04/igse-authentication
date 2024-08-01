package com.igse.repository;

import com.igse.dto.WalletInfoDTO;
import com.igse.repository.core.CoreError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

import static com.igse.util.GlobalConstant.BEARER;

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

    public WalletInfoDTO walletDetails(String customerId, String token) {
        return webClient.get()
                .uri(basePath + walletDetailPath + customerId + "/info")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                .retrieve()
                .onStatus(HttpStatus::isError, coreError::handleCoreError)
                .bodyToMono(WalletInfoDTO.class)
                .retryWhen(Retry
                        .fixedDelay(3, Duration.ofMillis(5000))
                        .doAfterRetry(x -> log.info("Total Retry {}", x.totalRetries()
                ))).block();
    }
}
