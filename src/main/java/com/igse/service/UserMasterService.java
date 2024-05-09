package com.igse.service;

import com.igse.config.EncoderDecoder;
import com.igse.dto.*;
import com.igse.entity.UserMaster;
import com.igse.exception.UserException;
import com.igse.repository.UserMasterRepository;
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

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserMasterService {
    private final WebClient webClient = WebClient.create();
    private final UserMasterRepository userMasterRepository;
    private final EncoderDecoder encoderDecoder;
    private final JwtService jwt;

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
        userMasterRepository.save(userDetails);
    }

    private VoucherResponse processVoucher(UserRegistrationDTO userRegistrationDTO) {
        VoucherResponse voucherDetails;
        IgseResponse<VoucherResponse> singleVoucher = singleDetail(userRegistrationDTO.getVoucherCode());
        if (Objects.nonNull(singleVoucher)) {
            voucherDetails = singleVoucher.getData();
            if (voucherDetails.getVoucherCode().equalsIgnoreCase(userRegistrationDTO.getVoucherCode())) {
                if (voucherDetails.getStatus().equals(GlobalConstant.Voucher.USED)) {
                    throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "EVC code already used");
                }
            } else {
                throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "Invalid EVC code");
            }
            VoucherResponse voucherCode = new VoucherResponse();
            voucherCode.setVoucherCode(voucherDetails.getVoucherCode());
            voucherCode.setStatus(GlobalConstant.Voucher.USED);
            voucherCode.setCustomerId(userRegistrationDTO.getCustomerId());
            voucherCode.setVoucherBalance(voucherDetails.getVoucherBalance());
            saveSingleDetail(voucherCode);
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

    public UserResponse getUserDetails(LoginRequest loginRequest) throws UserException {
        getUserDetail(loginRequest.getCustomerId());
        Optional.of(getUserDetail(loginRequest.getCustomerId())).orElse(null);
        UserMaster userDetails = userMasterRepository.findById(loginRequest.getCustomerId()).orElse(null);
        if (Objects.nonNull(userDetails)) {
            if (validateUser(userDetails, loginRequest)) {
                UserResponse userResponse = new UserResponse();
                userResponse.setToken(jwt.generateToken(userDetails.getCustomerId(), userDetails.getRole()));
                BeanUtils.copyProperties(userDetails, userResponse);
                userDetails.setLastLogin(LocalDate.now());
                userMasterRepository.save(userDetails);
                return userResponse;
            } else {
                throw new UserException(HttpStatus.NOT_FOUND.value(), "Invalid Password");
            }
        } else {
            throw new UserException(HttpStatus.NOT_FOUND.value(), "Customer not registered");
        }
    }

    private boolean validateUser(UserMaster userDetails, LoginRequest loginRequest) {
        return encoderDecoder.encrypt(loginRequest.getPassword()).equals(userDetails.getPass());
    }

    private IgseResponse<UserResponse> getUserDetail(String customerID) {
        return webClient.get()
                .uri("http://localhost:8080/igse/core/dashboard/user/info/" + customerID)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<IgseResponse<UserResponse>>() {
                }).block();
    }
}
