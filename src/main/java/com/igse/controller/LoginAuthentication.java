package com.igse.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.igse.dto.UserRegistrationDTO;
import com.igse.dto.LoginRequest;
import com.igse.dto.UserResponse;
import com.igse.dto.user.Login;
import com.igse.service.UserMasterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class LoginAuthentication {
    private static final Logger LOG = LoggerFactory.getLogger(LoginAuthentication.class);


    private final UserMasterService userMasterService;

    @PostMapping(path = "v1/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> loginAuthV1(
            @RequestBody @Validated(value = Login.LoginV1.class) @JsonView(value = Login.LoginV1.class) LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userMasterService.getUserDetails(loginRequest));
    }
    @PostMapping(path = "v2/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> loginAuthV2(
            @RequestBody @Valid @JsonView(value = Login.LoginV2.class) LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userMasterService.getUserDetails(loginRequest));
    }
    @PostMapping(path = "register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> userRegistration(
            @RequestBody @Valid UserRegistrationDTO userRegistrationDTO) {
        userMasterService.saveUser(userRegistrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
