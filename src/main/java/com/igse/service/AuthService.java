package com.igse.service;

import com.igse.config.EncoderDecoder;
import com.igse.dto.IgseResponse;
import com.igse.dto.UserResponse;
import com.igse.dto.login.LoginRequest;
import com.igse.entity.UserMaster;
import com.igse.exception.UserException;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final WebClient webClient = WebClient.create();
    private final UserMasterRepository userMasterRepository;
    private final EncoderDecoder encoderDecoder;
    private final JwtService jwt;

    public UserResponse getUserDetails(LoginRequest loginRequest) throws UserException {
       // getUserDetail(loginRequest.getCustomerId());
        //Optional.of(getUserDetail(loginRequest.getCustomerId())).orElse(null);
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
