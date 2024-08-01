package com.igse.repository.core;

import com.igse.dto.IgseResponse;
import com.igse.dto.MeterReadingDTO;
import com.igse.dto.UnitPriceDTO;
import com.igse.dto.VoucherResponse;
import com.igse.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
public class MeterRepo {

    private final WebClient webClient;
    private final JwtService jwtService;
    private final CoreError coreError;
    private static final String BASE_URL = "/meter/meter";
    @Value("${infrastructure.services.igse_core.endpoint}")
    private String basePath;
    @Value("${infrastructure.services.igse_core.changePricePath}")
    private String saveMeterPrincePath;

    @Value("${infrastructure.services.igse_core.meterDetailPath}")
    private String meterDetailsPath;

    public void saveMeterDetails(MeterReadingDTO readingDTO) {
        String token = jwtService.getAdminToken();
        webClient.post()
                .uri(basePath + BASE_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                .bodyValue(readingDTO)
                .retrieve()
                .onStatus(HttpStatus::isError, coreError::handleCoreError)
                .bodyToMono(new ParameterizedTypeReference<IgseResponse<VoucherResponse>>() {
                })
                .block();
        log.info("MeterReading Save Successfully");
    }

    public void saveMeterPriceDetail(UnitPriceDTO unitPriceDTO) {
        /*Note Please handle excetion incase 404*/
        String token = jwtService.getAdminToken();
        try {
            webClient.post()
                    .uri(basePath + saveMeterPrincePath)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                    .bodyValue(unitPriceDTO)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<IgseResponse<VoucherResponse>>() {
                    })
                    .block();
            log.info("Successfully Change Meter");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public IgseResponse<UnitPriceDTO> getFixedMeterDetails() {
        /*Note Please handle excetion incase 404*/
        String token = jwtService.getAdminToken();
        try {
            return webClient.get()
                    .uri(basePath + meterDetailsPath)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<IgseResponse<UnitPriceDTO>>() {
                    })
                    .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(1000)).doAfterRetry(x -> {
                        log.info("Total Retry {}", x.totalRetries()
                        );
                    }))
                    .block();

        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
