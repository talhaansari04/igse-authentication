package com.igse.service;

import com.igse.config.EncoderDecoder;
import com.igse.dto.IgseResponse;
import com.igse.dto.MeterReadingDTO;
import com.igse.dto.VoucherResponse;
import com.igse.dto.registration.UserRegistrationDTO;
import com.igse.entity.UserMaster;
import com.igse.entity.VoucherCodeEntity;
import com.igse.exception.UserException;
import com.igse.repository.UserMasterRepository;
import com.igse.repository.VoucherCodeRepository;
import com.igse.util.GlobalConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final WebClient webClient = WebClient.create();
    private final EncoderDecoder encoderDecoder;
    private final UserMasterRepository userMasterRepository;
    private final VoucherCodeRepository codeRepository;

    @Transactional
    public void saveUser(UserRegistrationDTO userRegistrationDTO) {
        Optional<UserMaster> customerDetails = userMasterRepository.findById(userRegistrationDTO.getCustomerId());
        if (customerDetails.isPresent()) {
            UserMaster details = customerDetails.get();
            if (details.getCustomerId().equalsIgnoreCase(userRegistrationDTO.getCustomerId())) {
                throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "Customer already exist");
            }
        }
        VoucherCodeEntity voucherDetails = processVoucher(userRegistrationDTO);
        UserMaster userDetails = new UserMaster();
        BeanUtils.copyProperties(userRegistrationDTO, userDetails);
        userDetails.setPass(encoderDecoder.encrypt(userRegistrationDTO.getPass()));
        userDetails.setCurrentBalance(voucherDetails.getVoucherBalance());
        userDetails.setRole(GlobalConstant.Role.USER);
        setMeterReadingInitialValue(userRegistrationDTO.getCustomerId());
        userMasterRepository.save(userDetails);
    }

    private VoucherCodeEntity processVoucher(UserRegistrationDTO userRegistrationDTO) {
        VoucherCodeEntity voucherDetails;
        Optional<VoucherCodeEntity> singleVoucher = codeRepository.findById(userRegistrationDTO.getVoucherCode());
        if (singleVoucher.isPresent()) {
            voucherDetails = singleVoucher.get();
            if (voucherDetails.getVoucherCode().equalsIgnoreCase(userRegistrationDTO.getVoucherCode())) {
                if (voucherDetails.getStatus().equals(GlobalConstant.Voucher.USED)) {
                    throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "EVC code already used");
                }
            } else {
                throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "Invalid EVC code");
            }
            VoucherCodeEntity voucherCode = VoucherCodeEntity.builder()
                    .voucherCode(voucherDetails.getVoucherCode())
                    .status(GlobalConstant.Voucher.USED)
                    .customerId(userRegistrationDTO.getCustomerId())
                    .voucherBalance(voucherDetails.getVoucherBalance()).build();
            codeRepository.save(voucherCode);
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

        webClient.post()
                .uri("http://localhost:8080/igse/core/meter")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(readingDTO)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<IgseResponse<VoucherResponse>>() {
                })
                .block();
        log.info("MeterReading Save Successfully");
    }

    private IgseResponse<VoucherResponse> singleDetail(String voucherCode) {
        /*Note Please handle excetion incase 404*/
        return webClient.get()
                .uri("http://localhost:8080/igse/core/admin/voucher/" + voucherCode)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<IgseResponse<VoucherResponse>>() {
                }).block();
    }

    private void saveSingleDetail(VoucherResponse voucherCode) {
        /*Note Please handle excetion incase 404*/
        webClient.patch()
                .uri("http://localhost:8080/igse/core/admin/voucher")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(voucherCode)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<IgseResponse<VoucherResponse>>() {
                })
                .block();
        log.info("Voucher Save Successfully");
    }

}
