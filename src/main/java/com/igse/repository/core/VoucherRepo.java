package com.igse.repository.core;

import static com.igse.util.IgseConstants.BEARER;
import static com.igse.util.IgseConstants.CORRELATION_ID;
import com.igse.dto.IgseResponse;
import com.igse.dto.VoucherResponse;
import com.igse.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Slf4j
@Component
@RequiredArgsConstructor
public class VoucherRepo {
    private final JwtService jwtService;
    private final WebClient webClient;
    private final CoreError coreError;
    @Value("${infrastructure.services.igse_core.endpoint}")
    private String basePath;

    @Value("${infrastructure.services.igse_core.voucherDetailsPath}")
    private String voucherDetailPath;

    @Value("${infrastructure.services.igse_core.saveVoucherPath}")
    private String saveVoucherPath;



    public IgseResponse<VoucherResponse> getVoucherDetail(String voucherCode, String correlationId) {
        String token = jwtService.getAdminToken();
        return webClient.get()
                .uri(basePath + voucherDetailPath + voucherCode)
                .header(CORRELATION_ID, correlationId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                .retrieve()
                .onStatus(HttpStatusCode::isError, coreError::handleCoreError)
                .bodyToMono(new ParameterizedTypeReference<IgseResponse<VoucherResponse>>() {
                }).block();
    }



    public void saveSingleDetail(VoucherResponse voucherCode, String correlationId) {
        String token = jwtService.getAdminToken();
        webClient.patch()
                .uri(basePath + saveVoucherPath)
                .header(CORRELATION_ID, correlationId)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                .bodyValue(voucherCode)
                .retrieve()
                .onStatus(HttpStatusCode::isError, coreError::handleCoreError)
                .bodyToMono(new ParameterizedTypeReference<IgseResponse<VoucherResponse>>() {
                })
                .block();
        log.info("Voucher Save Successfully");
    }
}
