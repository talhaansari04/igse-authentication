package com.igse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igse.config.EncoderDecoder;
import com.igse.dto.VoucherResponse;
import com.igse.dto.registration.Address;
import com.igse.dto.registration.DemographicDetails;
import com.igse.dto.registration.UserRegistrationDTO;
import com.igse.entity.DemographicDetailsEntity;
import com.igse.entity.RegistrationStatusEntity;
import com.igse.entity.UserMaster;
import com.igse.exception.UserException;
import com.igse.repository.RegistrationStatusRepo;
import com.igse.repository.UserMasterRepository;
import com.igse.repository.core.VoucherRepo;
import com.igse.util.GlobalConstant;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final EncoderDecoder encoderDecoder;
    private final UserMasterRepository userMasterRepository;
    private final VoucherRepo voucherRepo;
    private final RegistrationStatusRepo statusRepo;

    @Transactional
    public void saveUser(UserRegistrationDTO userRegistrationDTO) {
        Optional<UserMaster> customerDetails = userMasterRepository.findById(userRegistrationDTO.getCustomerId());
        if (customerDetails.isPresent()) {
            UserMaster details = customerDetails.get();
            if (details.getCustomerId().equalsIgnoreCase(userRegistrationDTO.getCustomerId())) {
                throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "Customer already exist");
            }
        }

        VoucherResponse voucherResponse = voucherDetails(userRegistrationDTO);
        UserMaster userMaster = mapUserAddress(userRegistrationDTO);
        UserMaster success = userMasterRepository.save(userMaster);
        RegistrationStatusEntity statusEntity = mapRegistrationStatus(userRegistrationDTO, voucherResponse);
        statusRepo.save(statusEntity);
       /* setMeterReadingInitialValue(userRegistrationDTO.getCustomerId());
        VoucherResponse voucherDetails = processVoucher(userRegistrationDTO);
        publishWalletEvent(success, voucherDetails);*/

    }

    @SneakyThrows
    private RegistrationStatusEntity mapRegistrationStatus(UserRegistrationDTO registrationDTO, VoucherResponse voucherResponse){
        return RegistrationStatusEntity.builder()
                .customerId(registrationDTO.getCustomerId())
                .jsonVoucherPayload(new ObjectMapper().writeValueAsString(voucherResponse))
                .isVoucherRedeemed("PENDING")
                .isMeterDetailSave("PENDING")
                .isWalletCreated("PENDING").build();
    }

    private UserMaster mapUserAddress(UserRegistrationDTO registrationDTO) {
        DemographicDetails details = registrationDTO.getDemographicDetails();
        Address address = details.getAddress();
        DemographicDetailsEntity demographicDetails=DemographicDetailsEntity.builder()
                .customerId(registrationDTO.getCustomerId())
                .addressLandmark(address.getLandmark())
                .addressArea(address.getArea())
                .addressFlatNo(address.getFlatNo())
                .addressPinCode(address.getPinCode())
                .numberOfBedRoom(details.getNumberOfBedRoom())
                .propertyType(details.getPropertyType())
                .flatRegistrationNo(details.getFlatRegistrationNo())
                .build();

       return UserMaster.builder()
                .userName("-")
                .customerId(registrationDTO.getCustomerId())
                .pass(encoderDecoder.encrypt(registrationDTO.getPass()))
                .role(GlobalConstant.Role.USER)
                .demographicDetails(demographicDetails).build();
    }

    private VoucherResponse voucherDetails(UserRegistrationDTO userRegistrationDTO){
        VoucherResponse voucherDetails = Optional
                .of(voucherRepo.getVoucherDetail(userRegistrationDTO.getVoucherCode())
                        .getData())
                .orElseThrow(() -> new UserException(HttpStatus.ALREADY_REPORTED.value(), "Invalid EVC code"));
        if (voucherDetails.getVoucherCode().equalsIgnoreCase(userRegistrationDTO.getVoucherCode())) {
            if (voucherDetails.getStatus().equals(GlobalConstant.USED)) {
                throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "EVC code already used");
            }else {
                return voucherDetails;
            }
        } else {
            throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "Invalid EVC code");
        }
    }







}
