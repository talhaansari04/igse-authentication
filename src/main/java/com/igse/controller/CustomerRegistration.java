package com.igse.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.igse.dto.registration.RegistrationVersion;
import com.igse.dto.registration.UserRegistrationDTO;
import com.igse.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerRegistration {

    private final CustomerService userMasterService;
    @PostMapping(path = "v1/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> userRegistrationV1(
            @RequestBody @Valid @JsonView(RegistrationVersion.V1.class) UserRegistrationDTO userRegistrationDTO) {
        userMasterService.saveUser(userRegistrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping(path = "v2/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> userRegistrationV2(
            @RequestBody @Validated(RegistrationVersion.V2.class)
            @JsonView(RegistrationVersion.V2.class) UserRegistrationDTO userRegistrationDTO) {
        userMasterService.saveUser(userRegistrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
