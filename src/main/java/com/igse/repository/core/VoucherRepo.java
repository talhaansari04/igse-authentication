package com.igse.repository.core;

import com.igse.dto.IgseResponse;
import com.igse.dto.VoucherResponse;
import com.igse.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoucherRepo {
    private final JwtService jwtService;
    private final WebClient webClient = WebClient.create();
    private static final String BASE_URL="http://localhost:8080/igse/core/voucher/";

    public IgseResponse<VoucherResponse> getVoucherDetail(String voucherCode) {
        String token = jwtService.getAdminToken();
        /*Note Please handle excetion incase 404*/
        return webClient.get()
                .uri(BASE_URL + voucherCode)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<IgseResponse<VoucherResponse>>() {
                }).block();
    }

    public void saveSingleDetail(VoucherResponse voucherCode) {
        /*Note Please handle excetion incase 404*/
        String token = jwtService.getAdminToken();
        webClient.patch()
                .uri(BASE_URL+"save")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
                .bodyValue(voucherCode)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<IgseResponse<VoucherResponse>>() {
                })
                .block();
        log.info("Voucher Save Successfully");
    }
}
