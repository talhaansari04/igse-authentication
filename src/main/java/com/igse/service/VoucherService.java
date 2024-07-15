package com.igse.service;

import com.igse.dto.payment.IgsePaymentRequest;
import com.igse.dto.payment.IgsePaymentResponse;
import com.igse.entity.VoucherCodeEntity;
import com.igse.exception.UserException;
import com.igse.repository.VoucherCodeRepository;
import com.igse.util.GlobalConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoucherService {
    private final VoucherCodeRepository codeRepository;

    public void createVoucherByScheduler() {
        long count = codeRepository.countByStatus(GlobalConstant.Voucher.NOT_USED);
        if (count > 10) {
            log.info("No need to create voucher code...{}", count);
        } else {
            log.info("Number of voucher code...{}", count);
            createVoucher(200.00);
            log.info("Voucher Created...");
        }

    }

    private VoucherCodeEntity createVoucher(double amount) {
        VoucherCodeEntity codeEntity = VoucherCodeEntity.builder()
                .voucherCode(randomString())
                .voucherBalance(amount)
                .status(GlobalConstant.Voucher.NOT_USED).build();
        return codeRepository.save(codeEntity);

    }

    /* Should use pagignation*/
    public List<VoucherCodeEntity> getAllVoucher(int offSet, int pageSize, String status) {
        PageRequest pageReq = PageRequest.of(offSet, pageSize);
        if (GlobalConstant.Voucher.NOT_USED.contains(status)) {
            return codeRepository.findByStatus(status, pageReq);
        } else {
            return codeRepository.findAll(pageReq).getContent();
        }
    }

    private String randomString() {
        return RandomStringUtils.randomAlphanumeric(8).toUpperCase();
    }

    public IgsePaymentResponse buyVoucherCode(IgsePaymentRequest paymentRequest) {
        VoucherCodeEntity voucher;
        if (verifyByPaytm(paymentRequest.getTransactionId())) {
            Optional<VoucherCodeEntity> voucherCode = codeRepository.getVoucherCodeByAmount(paymentRequest.getAmount());
            voucher = voucherCode.orElseGet(() -> createVoucher(paymentRequest.getAmount()));
        } else {
            throw new UserException(HttpStatus.NOT_FOUND.value(), "Invalid");
        }
        voucher.setStatus(GlobalConstant.Voucher.USED);
        codeRepository.save(voucher);
        return IgsePaymentResponse.builder()
                .voucherCode(voucher.getVoucherCode())
                .expiryDate(LocalDate.now().plusMonths(2).toString()).build();
    }


    private boolean verifyByPaytm(String transactionId) {
        return true;
    }
}
