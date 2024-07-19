package com.igse.repository.core;

import com.igse.dto.IgseResponse;
import com.igse.dto.MeterReadingDTO;
import com.igse.dto.VoucherResponse;
import com.igse.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeterRepo {

    private final WebClient webClient = WebClient.create();
    private final JwtService jwtService;
    private static final String BASE_URL="http://localhost:8080/igse/core/meter";

    public void saveMeterDetails(MeterReadingDTO readingDTO) {
        String token = jwtService.getAdminToken();
        webClient.post()
                .uri(BASE_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
                .bodyValue(readingDTO)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<IgseResponse<VoucherResponse>>() {
                })
                .block();
        log.info("MeterReading Save Successfully");
    }
}
