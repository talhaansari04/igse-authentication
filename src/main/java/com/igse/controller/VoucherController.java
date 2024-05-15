package com.igse.controller;

import com.igse.dto.VoucherDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class VoucherController {
    @PostMapping(path = "create/voucher", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createVoucher(@RequestBody @Valid VoucherDTO voucherDTO) {
       // adminServices.createEVC(voucherDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
