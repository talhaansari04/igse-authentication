package com.igse.controller;

import static com.igse.common.IgseConstants.CORRELATION_ID;
import com.fasterxml.jackson.annotation.JsonView;
import com.igse.dto.registration.RegistrationVersion;
import com.igse.dto.registration.UserRegRequest;
import com.igse.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerRegistration {

    private final CustomerService userMasterService;

    @PostMapping(path = "v1/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> userRegistrationV1(@RequestHeader(CORRELATION_ID) String correlationId,
                                                     @RequestBody @Valid @JsonView(RegistrationVersion.V1.class) UserRegRequest userRegRequest) {
        MDC.put(CORRELATION_ID, correlationId);
        log.info("message=\" Customer Registration requestV1 received");
        userMasterService.saveUser(userRegRequest, correlationId);
        MDC.clear();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(path = "v2/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> userRegistrationV2(
            @RequestHeader(CORRELATION_ID) String correlationId,
            @RequestBody @Validated(RegistrationVersion.V2.class)
            @JsonView(RegistrationVersion.V2.class) UserRegRequest userRegRequest) {
        MDC.put(CORRELATION_ID, correlationId);
        userMasterService.saveUser(userRegRequest, correlationId);
        MDC.clear();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
