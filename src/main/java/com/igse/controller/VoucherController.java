package com.igse.controller;

import com.igse.dto.IgseResponse;
import com.igse.dto.VoucherDTO;
import com.igse.entity.VoucherCodeEntity;
import com.igse.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class VoucherController {
    private final VoucherService voucherService;
    @PostMapping(path = "create/voucher", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createVoucher(@RequestBody @Valid VoucherDTO voucherDTO) {
       // adminServices.createEVC(voucherDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping(path = "voucher/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IgseResponse<List<VoucherCodeEntity>>> getAllVoucher() {
        IgseResponse<List<VoucherCodeEntity>> igseResponse=new IgseResponse<>();
        igseResponse.setData(voucherService.getAllVoucher());
        igseResponse.setStatus(HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(igseResponse);
    }
}
