package com.igse.service;

import com.igse.config.EncoderDecoder;
import com.igse.dto.MeterReadingDTO;
import com.igse.dto.VoucherResponse;
import com.igse.dto.WalletPayloadKafka;
import com.igse.dto.registration.Address;
import com.igse.dto.registration.DemographicDetails;
import com.igse.dto.registration.UserRegistrationDTO;
import com.igse.entity.DemographicDetailsEntity;
import com.igse.entity.UserMaster;
import com.igse.exception.UserException;
import com.igse.repository.UserMasterRepository;
import com.igse.repository.core.MeterRepo;
import com.igse.repository.core.VoucherRepo;
import com.igse.util.GlobalConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final EncoderDecoder encoderDecoder;
    private final UserMasterRepository userMasterRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final VoucherRepo voucherRepo;
    private final MeterRepo meterRepo;

    @Transactional
    public void saveUser(UserRegistrationDTO userRegistrationDTO) {
        Optional<UserMaster> customerDetails = userMasterRepository.findById(userRegistrationDTO.getCustomerId());
        if (customerDetails.isPresent()) {
            UserMaster details = customerDetails.get();
            if (details.getCustomerId().equalsIgnoreCase(userRegistrationDTO.getCustomerId())) {
                throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "Customer already exist");
            }
        }
        VoucherResponse voucherDetails = processVoucher(userRegistrationDTO);
        UserMaster userMaster = mapUserAddress(userRegistrationDTO);
       // userDetails.setPass(encoderDecoder.encrypt(userRegistrationDTO.getPass()));
        //userDetails.setRole(GlobalConstant.Role.USER);
        setMeterReadingInitialValue(userRegistrationDTO.getCustomerId());
        UserMaster success = userMasterRepository.save(userMaster);
        publishWalletEvent(success, voucherDetails);

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

    private void publishWalletEvent(UserMaster userMaster, VoucherResponse voucherDetails) {
        WalletPayloadKafka wallet = WalletPayloadKafka.builder()
                .customerId(userMaster.getCustomerId())
                .totalBalance(voucherDetails.getVoucherBalance())
                .creationDate(LocalDate.now()).build();
        eventPublisher.publishEvent(wallet);
    }

    private VoucherResponse processVoucher(UserRegistrationDTO userRegistrationDTO) {
        VoucherResponse voucherDetails = Optional
                .of(voucherRepo.getVoucherDetail(userRegistrationDTO.getVoucherCode())
                        .getData())
                .orElseThrow(() -> new UserException(HttpStatus.ALREADY_REPORTED.value(), "Invalid EVC code"));
        if (voucherDetails.getVoucherCode().equalsIgnoreCase(userRegistrationDTO.getVoucherCode())) {
            if (voucherDetails.getStatus().equals(GlobalConstant.USED)) {
                throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "EVC code already used");
            }
        } else {
            throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "Invalid EVC code");
        }
        VoucherResponse voucherCode = VoucherResponse.builder()
                .voucherCode(voucherDetails.getVoucherCode())
                .status(GlobalConstant.USED)
                .customerId(userRegistrationDTO.getCustomerId())
                .voucherBalance(voucherDetails.getVoucherBalance()).build();
        voucherRepo.saveSingleDetail(voucherCode);
        return voucherDetails;
    }

    private void setMeterReadingInitialValue(String customerId) {
        MeterReadingDTO readingDTO = MeterReadingDTO.builder()
                .dayReading(100.00)
                .nightReading(250.0)
                .gasReading(800.00)
                .submissionDate(LocalDate.now())
                .billingStatus(GlobalConstant.PAID)
                .customerId(customerId).build();
        meterRepo.saveMeterDetails(readingDTO);
    }


}
