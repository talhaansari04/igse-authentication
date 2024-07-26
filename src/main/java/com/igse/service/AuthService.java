package com.igse.service;

import com.igse.config.EncoderDecoder;
import com.igse.dto.IgseResponse;
import com.igse.dto.UserResponse;
import com.igse.dto.WalletInfoDTO;
import com.igse.dto.login.LoginRequest;
import com.igse.entity.UserMaster;
import com.igse.exception.UserException;
import com.igse.repository.PaymentRepo;
import com.igse.repository.UserMasterRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String INVALID_USER = "Customer not registered";
    private final WebClient webClient = WebClient.create();
    private final UserMasterRepository userMasterRepository;
    private final EncoderDecoder encoderDecoder;
    private final JwtService jwt;
    private final PaymentRepo paymentRepo;

    public UserResponse v1Login(LoginRequest loginRequest) {
        UserMaster userDetails = userMasterRepository.findById(loginRequest.getCustomerId())
                .orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND.value(), INVALID_USER));
        return getUserDetails(loginRequest, userDetails);

    }

    public UserResponse v2Login(LoginRequest loginRequest) {
        UserMaster userDetails = userMasterRepository.findAllByUserName(loginRequest.getUserName())
                .orElseThrow(() -> new UserException(HttpStatus.NOT_FOUND.value(), INVALID_USER));
        return getUserDetails(loginRequest, userDetails);
    }


    private UserResponse getUserDetails(LoginRequest loginRequest, UserMaster userDetails) throws UserException {
            if (validateUser(userDetails, loginRequest)) {
                UserResponse userResponse = new UserResponse();
                String token = jwt.generateToken(userDetails.getCustomerId(), userDetails.getRole());
                userResponse.setToken(token);
                BeanUtils.copyProperties(userDetails, userResponse);
                userDetails.setLastLogin(LocalDate.now());
                userMasterRepository.save(userDetails);
                WalletInfoDTO walletInfo = paymentRepo.walletDetails(userDetails.getCustomerId(),token);
                userResponse.setWalletInfo(walletInfo);
                return userResponse;
            } else {
                throw new UserException(HttpStatus.NOT_FOUND.value(), "Invalid Credential");
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
