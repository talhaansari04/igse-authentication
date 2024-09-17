package com.igse.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.igse.dto.login.LoginRequest;
import com.igse.dto.UserResponse;
import com.igse.dto.login.LoginVersion;
import com.igse.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginAuthentication {


    private final AuthService authService;

    @PostMapping(path = "v1/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<UserResponse> loginAuthV1(
            @RequestBody @Validated(value = LoginVersion.LoginV1.class)
            @JsonView(value = LoginVersion.LoginV1.class) final LoginRequest loginRequest) {
        log.info("Login Authentication Start {}", loginRequest.getCustomerId());
        return ResponseEntity.status(HttpStatus.OK).body(authService.v1Login(loginRequest));
    }

    @PostMapping(path = "v2/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public final ResponseEntity<UserResponse> loginAuthV2(
            @RequestBody @Validated(value = LoginVersion.LoginV2.class)
            @JsonView(value = LoginVersion.LoginV2.class) LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.v2Login(loginRequest));
    }

}
