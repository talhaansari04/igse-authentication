package com.igse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igse.common.IgseConstants;
import com.igse.config.EncoderDecoder;
import com.igse.dto.VoucherResponse;
import com.igse.dto.registration.Address;
import com.igse.dto.registration.DemographicDetails;
import com.igse.dto.registration.UserRegRequest;
import com.igse.entity.DemographicDetailsEntity;
import com.igse.entity.RegistrationStatusEntity;
import com.igse.entity.UserMaster;
import com.igse.exception.UserException;
import com.igse.repository.core.VoucherRepo;
import com.igse.repository.db.RegistrationStatusRepo;
import com.igse.repository.db.UserMasterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.igse.common.IgseConstants.PENDING;
import static com.igse.common.IgseConstants.USED;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final EncoderDecoder encoderDecoder;
    private final UserMasterRepository userMasterRepository;
    private final VoucherRepo voucherRepo;
    private final RegistrationStatusRepo statusRepo;

    @Transactional
    public void saveUser(UserRegRequest userRegRequest) {
        Optional<UserMaster> customerDetails = userMasterRepository.findById(userRegRequest.getCustomerId());
        if (customerDetails.isPresent()) {
            UserMaster details = customerDetails.get();
            if (details.getCustomerId().equalsIgnoreCase(userRegRequest.getCustomerId())) {
                throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "Customer already exist");
            }
        }
        log.info("message=\"New customer registration process start ... {}",userRegRequest.getCustomerId());
        VoucherResponse voucherResponse = voucherDetails(userRegRequest);
        UserMaster success = userMasterRepository.save(mapUserAddress(userRegRequest));
        statusRepo.save(mapRegistrationStatus(userRegRequest, voucherResponse));
        log.info("message=\"Customer Registered Successfully ... {}",success.getCustomerId());
    }

    @SneakyThrows
    private RegistrationStatusEntity mapRegistrationStatus(UserRegRequest registrationDTO, VoucherResponse voucherResponse){
        return RegistrationStatusEntity.builder()
                .customerId(registrationDTO.getCustomerId())
                .jsonVoucherPayload(new ObjectMapper().writeValueAsString(voucherResponse))
                .isVoucherRedeemed(PENDING)
                .isMeterDetailSave(PENDING)
                .isWalletCreated(PENDING).build();
    }

    private UserMaster mapUserAddress(UserRegRequest registrationDTO) {
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
                .role(IgseConstants.Role.USER)
                .demographicDetails(demographicDetails).build();
    }

    private VoucherResponse voucherDetails(UserRegRequest userRegRequest){
        log.info("message=\"Fetching voucher details {}",userRegRequest.getCustomerId());
        VoucherResponse voucherDetails = Optional
                .of(voucherRepo.getVoucherDetail(userRegRequest.getVoucherCode())
                        .getData())
                .orElseThrow(() -> new UserException(HttpStatus.ALREADY_REPORTED.value(), "Invalid EVC code"));
        if (voucherDetails.getVoucherCode().equalsIgnoreCase(userRegRequest.getVoucherCode())) {
            if (voucherDetails.getStatus().equals(USED)) {
                log.info("message=\"Voucher validation failed for {}",userRegRequest.getCustomerId());
                throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "EVC code already used");
            }else {
                return voucherDetails;
            }
        } else {
            throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "Invalid EVC code");
        }
    }
}
