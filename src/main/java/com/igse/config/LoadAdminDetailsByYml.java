package com.igse.config;

import com.igse.dto.IgseResponse;
import com.igse.dto.UnitPriceDTO;
import com.igse.entity.DemographicDetailsEntity;
import com.igse.entity.UserMaster;
import com.igse.repository.UserMasterRepository;
import com.igse.repository.core.MeterRepo;
import com.igse.service.JwtService;
import com.igse.util.GlobalConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
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

    @Value("${igse.admin.day}")
    private double dayPrice;

    @Value("${igse.admin.night}")
    private double nightPrice;

    @Value("${igse.admin.gas}")
    private double gas;

    private final UserMasterRepository repository;
    private final EncoderDecoder encoderDecoder;
    private final MeterRepo meterRepo;
    private static final String BLANK = "-";

    @Override
    public void run(String... args) {
        Optional<UserMaster> details = repository.findById(adminID);
        if (details.isEmpty()) {
            repository.save(mapUserAddress());
        }
        IgseResponse<UnitPriceDTO> adminData = meterRepo.getFixedMeterDetails();
        if (Objects.isNull(adminData)) {
            UnitPriceDTO priceEntity = new UnitPriceDTO();
            priceEntity.setCustomerId(adminID);
            priceEntity.setDayCharge(dayPrice);
            priceEntity.setNightCharge(nightPrice);
            priceEntity.setGasCharge(gas);
            priceEntity.setStandingChargePerDay(0.74);
            meterRepo.saveMeterPriceDetail(priceEntity);
        } else {
            log.info(adminData.toString());
        }
    }

    private UserMaster mapUserAddress() {

        DemographicDetailsEntity demographicDetails = DemographicDetailsEntity.builder()
                .customerId(adminID)
                .addressLandmark(BLANK)
                .addressArea(BLANK)
                .addressFlatNo(BLANK)
                .addressPinCode(110024L)
                .numberOfBedRoom(0)
                .propertyType(BLANK)
                .flatRegistrationNo(BLANK)
                .build();

        return UserMaster.builder()
                .customerId(adminID)
                .pass(encoderDecoder.encrypt(adminPass))
                .userName(GlobalConstant.Role.ADMIN)
                .role(GlobalConstant.Role.ADMIN)
                .demographicDetails(demographicDetails).build();
    }




}
