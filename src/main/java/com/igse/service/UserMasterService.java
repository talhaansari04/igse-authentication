package com.igse.service;

import com.igse.config.EncoderDecoder;
import com.igse.dto.IgseResponse;
import com.igse.dto.UserRegistrationDTO;
import com.igse.dto.UserRequest;
import com.igse.dto.UserResponse;
import com.igse.entity.MeterReadingEntity;
import com.igse.entity.UserMaster;
import com.igse.entity.VoucherCodeEntity;
import com.igse.exception.UserException;
import com.igse.repository.MeterReadingRepo;
import com.igse.repository.UserMasterRepository;
import com.igse.repository.VoucherRepo;
import com.igse.util.GlobalConstant;
import lombok.RequiredArgsConstructor;
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
public class UserMasterService {
    private final WebClient webClient = WebClient.create();
    private final UserMasterRepository userMasterRepository;
    private final VoucherRepo voucherRepo;
    private final MeterReadingRepo readingRepo;
    private final EncoderDecoder encoderDecoder;
    private final JwtService jwt;

    private IgseResponse<VoucherCodeEntity> singleDetail(String voucherCode){
        /*Note Please handle excetion incase 404*/
       return webClient.get()
                .uri("http://localhost:8080/igse/core/admin/voucher/" + voucherCode)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<IgseResponse<VoucherCodeEntity>>() {
                }).block();
    }

    public void saveUser(UserRegistrationDTO userRegistrationDTO) {
        VoucherCodeEntity voucherDetails;
        Optional<UserMaster> customerDetails = userMasterRepository.findById(userRegistrationDTO.getCustomerId());
        if (customerDetails.isPresent()) {
            UserMaster details = customerDetails.get();
            if (details.getCustomerId().equalsIgnoreCase(userRegistrationDTO.getCustomerId())) {
                throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "Customer already exist");
            }
        }
        IgseResponse<VoucherCodeEntity> singleVoucher = singleDetail(userRegistrationDTO.getVoucherCode());
        if (Objects.nonNull(singleVoucher)) {
            voucherDetails = singleVoucher.getData();
            if (voucherDetails.getVoucherCode().equalsIgnoreCase(userRegistrationDTO.getVoucherCode())) {
                if (voucherDetails.getStatus().equals(GlobalConstant.Voucher.USED)) {
                    throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "EVC code already used");
                }
            } else {
                throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "Invalid EVC code");
            }
            VoucherCodeEntity voucherCode = new VoucherCodeEntity();
            voucherCode.setVoucherCode(voucherDetails.getVoucherCode());
            voucherCode.setStatus(GlobalConstant.Voucher.USED);
            voucherCode.setCustomerId(userRegistrationDTO.getCustomerId());
            voucherCode.setVoucherBalance(voucherDetails.getVoucherBalance());
            voucherRepo.save(voucherCode);
        } else {
            throw new UserException(HttpStatus.ALREADY_REPORTED.value(), "Invalid EVC code");
        }
        UserMaster userDetails = new UserMaster();
        BeanUtils.copyProperties(userRegistrationDTO, userDetails);
        userDetails.setPass(encoderDecoder.encrypt(userRegistrationDTO.getPass()));
        userDetails.setCurrentBalance(voucherDetails.getVoucherBalance());
        userDetails.setRole(GlobalConstant.Role.USER);
        setMeterReadingInitialValue(userRegistrationDTO.getCustomerId());
        userMasterRepository.save(userDetails);
    }

    private void setMeterReadingInitialValue(String customerId) {
        MeterReadingEntity readingEntity = new MeterReadingEntity();
        readingEntity.setDayReading(100.00);
        readingEntity.setNightReading(250.0);
        readingEntity.setGasReading(800.00);
        readingEntity.setSubmissionDate(LocalDate.now());
        readingEntity.setBillingStatus(GlobalConstant.PAID);
        readingEntity.setCustomerId(customerId);
        readingEntity.setPaidAmount(0.0);
        readingEntity.setDueAmount(0.0);
        readingEntity.setPaymentDate(LocalDate.now());
        readingRepo.save(readingEntity);
    }

    public UserResponse getUserDetails(UserRequest userRequest) throws UserException {
        getUserDetail(userRequest.getCustomerId());
        Optional.of(getUserDetail(userRequest.getCustomerId())).orElse(null);
        UserMaster userDetails = userMasterRepository.findById(userRequest.getCustomerId()).orElse(null);
        if (Objects.nonNull(userDetails)) {
            if (validateUser(userDetails, userRequest)) {
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

    private boolean validateUser(UserMaster userDetails, UserRequest userRequest) {
        return encoderDecoder.encrypt(userRequest.getPassword()).equals(userDetails.getPass());
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
