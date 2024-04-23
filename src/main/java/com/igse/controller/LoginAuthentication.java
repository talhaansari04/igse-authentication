package com.igse.controller;


import com.igse.dto.UserRegistrationDTO;
import com.igse.dto.UserRequest;
import com.igse.dto.UserResponse;
import com.igse.service.UserMasterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class LoginAuthentication {
    private static final Logger LOG = LoggerFactory.getLogger(LoginAuthentication.class);


    private final UserMasterService userMasterService;

    @PostMapping(path = "login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> loginAuth(
            @RequestBody @Valid UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userMasterService.getUserDetails(userRequest));
    }

    @PostMapping(path = "register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> userRegistration(
            @RequestBody @Valid UserRegistrationDTO userRegistrationDTO) {
        userMasterService.saveUser(userRegistrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
