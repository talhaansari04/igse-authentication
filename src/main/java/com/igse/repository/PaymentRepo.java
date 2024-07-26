package com.igse.repository;

import com.igse.dto.WalletInfoDTO;
import com.igse.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRepo {
    private final WebClient webClient = WebClient.create();
    private final JwtService jwtService;
    private static final String BASE_URL = "http://localhost:8080/igse-payment/wallet/v1/";

    public WalletInfoDTO walletDetails(String customerId,String token) {
        return webClient.get()
                .uri(BASE_URL + customerId + "/info")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(WalletInfoDTO.class)
                .block();
    }
}
