package com.igse.service;

import com.igse.config.EncoderDecoder;
import com.igse.dto.MeterReadingDTO;
import com.igse.dto.VoucherResponse;
import com.igse.dto.registration.UserRegistrationDTO;
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
        UserMaster userDetails = new UserMaster();
        BeanUtils.copyProperties(userRegistrationDTO, userDetails);
        userDetails.setPass(encoderDecoder.encrypt(userRegistrationDTO.getPass()));
        userDetails.setCurrentBalance(voucherDetails.getVoucherBalance());
        userDetails.setRole(GlobalConstant.Role.USER);
        setMeterReadingInitialValue(userRegistrationDTO.getCustomerId());
        UserMaster success = userMasterRepository.save(userDetails);
        eventPublisher.publishEvent(success);
    }

    private VoucherResponse processVoucher(UserRegistrationDTO userRegistrationDTO) {
        VoucherResponse voucherDetails;
        Optional<VoucherResponse> singleVoucher = Optional.of(voucherRepo.getVoucherDetail(userRegistrationDTO.getVoucherCode()).getData());
        if (singleVoucher.isPresent()) {
            voucherDetails = singleVoucher.get();
            if (voucherDetails.getVoucherCode().equalsIgnoreCase(userRegistrationDTO.getVoucherCode())) {
                if (voucherDetails.getStatus().equals(GlobalConstant.Voucher.USED)) {
                    throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "EVC code already used");
                }
            } else {
                throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "Invalid EVC code");
            }
            VoucherResponse voucherCode = VoucherResponse.builder()
                    .voucherCode(voucherDetails.getVoucherCode())
                    .status(GlobalConstant.Voucher.USED)
                    .customerId(userRegistrationDTO.getCustomerId())
                    .voucherBalance(voucherDetails.getVoucherBalance()).build();
            voucherRepo.saveSingleDetail(voucherCode);
        } else {
            throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "Invalid EVC code");
        }
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
