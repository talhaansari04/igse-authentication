package com.igse.controller;

import com.igse.dto.IgseResponse;
import com.igse.dto.VoucherDTO;
import com.igse.dto.payment.IgsePaymentRequest;
import com.igse.dto.payment.IgsePaymentResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class VoucherController {
    private final VoucherService voucherService;

    @PostMapping(path = "voucher/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createVoucher(@RequestBody @Valid VoucherDTO voucherDTO) {
        // adminServices.createEVC(voucherDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(path = "voucher/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IgseResponse<List<VoucherCodeEntity>>> getAllVoucher(
            @RequestParam(value = "offset") int offset,
            @RequestParam(value = "page", defaultValue = "10") int pageSize,
            @RequestParam(name = "status", defaultValue = "ALL") String status) {
        IgseResponse<List<VoucherCodeEntity>> igseResponse = new IgseResponse<>();
        igseResponse.setData(voucherService.getAllVoucher(offset, pageSize,status));
        igseResponse.setStatus(HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(igseResponse);
    }

    @PostMapping(path = "voucher/buy", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IgsePaymentResponse> buyVoucher(@RequestBody @Valid IgsePaymentRequest igsePaymentRequest) {
        log.info("BuyVoucherRequest received {}", igsePaymentRequest);
        IgsePaymentResponse response = voucherService.buyVoucherCode(igsePaymentRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}
