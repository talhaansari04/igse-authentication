package com.igse.service;

import static com.igse.util.IgseConstants.PENDING;
import static com.igse.util.IgseConstants.USED;
import static com.igse.util.ErrorCode.EVC_COUPON_INVALID;
import static com.igse.util.ErrorCode.EVC_COUPON_USED;
import static com.igse.util.ErrorCode.USER_ALREADY_EXIST;
import static com.igse.util.ErrorCode.USER_NOT_FOUND;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igse.util.IgseConstants;
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
    public void saveUser(UserRegRequest userRegRequest,String correlationId) {
        Optional<UserMaster> customerDetails =
                userMasterRepository.findById(userRegRequest.getCustomerId());
        if (customerDetails.isPresent()) {
            UserMaster details = customerDetails.get();
            if (details.getCustomerId().equalsIgnoreCase(userRegRequest.getCustomerId())) {
                throw new UserException(USER_ALREADY_EXIST.getCode(),
                        USER_ALREADY_EXIST.getMessage());
            }
        }
        log.info("message=\"New customer registration process start ... {}",
                userRegRequest.getCustomerId());
        VoucherResponse voucherResponse = voucherDetails(userRegRequest,correlationId);
        UserMaster success = userMasterRepository.save(mapUserAddress(userRegRequest));
        statusRepo.save(mapRegistrationStatus(userRegRequest, voucherResponse));
        log.info("message=\"Customer Registered Successfully ... {}", success.getCustomerId());
    }

    @SneakyThrows
    private RegistrationStatusEntity mapRegistrationStatus(UserRegRequest registrationDTO,
                                                           VoucherResponse voucherResponse) {
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
        DemographicDetailsEntity demographicDetails = DemographicDetailsEntity.builder()
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

    private VoucherResponse voucherDetails(UserRegRequest userRegRequest,String correlationId) {
        log.info("message=\"Fetching voucher details {}", userRegRequest.getCustomerId());
        VoucherResponse voucherDetails = Optional
                .of(voucherRepo.getVoucherDetail(userRegRequest.getVoucherCode(),correlationId)
                        .getData())
                .orElseThrow(() -> new UserException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage()));

        if (voucherDetails.getVoucherCode().equalsIgnoreCase(userRegRequest.getVoucherCode())) {
            if (voucherDetails.getStatus().equals(USED)) {
                log.info("message=\"Voucher validation failed for {}", userRegRequest.getCustomerId());
                throw new UserException(EVC_COUPON_USED.getCode(), EVC_COUPON_USED.getMessage());
            } else {
                return voucherDetails;
            }
        } else {
            log.info("message=\"Invalid voucher code {}", userRegRequest.getCustomerId());
            throw new UserException(EVC_COUPON_INVALID.getCode(), EVC_COUPON_INVALID.getMessage());
        }
    }
}
