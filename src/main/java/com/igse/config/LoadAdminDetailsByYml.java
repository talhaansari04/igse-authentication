package com.igse.config;

import com.igse.dto.IgseResponse;
import com.igse.dto.UnitPriceDTO;
import com.igse.dto.VoucherResponse;
import com.igse.entity.UserMaster;
import com.igse.repository.UserMasterRepository;
import com.igse.service.JwtService;
import com.igse.util.GlobalConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoadAdminDetailsByYml implements CommandLineRunner {
    @Value("${igse.admin.id}")
    private String adminID;
    @Value("${igse.admin.pass}")
    private String adminPass;

    private final UserMasterRepository repository;
    private final EncoderDecoder encoderDecoder;

    private final JwtService jwtService;
    private final WebClient webClient = WebClient.create();
    @Override
    public void run(String... args) {
        Optional<UserMaster> details = repository.findById(adminID);
        if (details.isEmpty()) {
            UserMaster userMaster = new UserMaster();
            userMaster.setRole(GlobalConstant.Role.ADMIN);
            userMaster.setCustomerId(adminID);
            userMaster.setPass(encoderDecoder.encrypt(adminPass));
            repository.save(userMaster);
        }
        IgseResponse<UnitPriceDTO> adminData = getFixedMeterDetails();
        if (Objects.isNull(adminData)) {
            UnitPriceDTO priceEntity = new UnitPriceDTO();
            priceEntity.setCustomerId(adminID);
            priceEntity.setDayCharge(0.34);
            priceEntity.setNightCharge(0.2);
            priceEntity.setGasCharge(0.1);
            priceEntity.setStandingChargePerDay(0.74);
            saveSingleDetail(priceEntity);
        }else {
            log.info(adminData.toString());
        }
    }
    private IgseResponse<UnitPriceDTO> getFixedMeterDetails() {
        /*Note Please handle excetion incase 404*/
        String token = jwtService.getAdminToken();
        try {
            return webClient.get()
                    .uri("http://localhost:8080/igse/core/admin/meter/price")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<IgseResponse<UnitPriceDTO>>() {
                    }).block();

        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }

    }
    private void saveSingleDetail(UnitPriceDTO unitPriceDTO) {
        /*Note Please handle excetion incase 404*/
        String token = jwtService.getAdminToken();
        try {
            webClient.post()
                    .uri("http://localhost:8080/igse/core/change/price")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION,"Bearer "+token)
                    .bodyValue(unitPriceDTO)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<IgseResponse<VoucherResponse>>() {
                    })
                    .block();
            log.info("Successfully Change Meter");
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
