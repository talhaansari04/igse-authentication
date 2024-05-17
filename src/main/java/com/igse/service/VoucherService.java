package com.igse.service;

import com.igse.entity.VoucherCodeEntity;
import com.igse.repository.VoucherCodeRepository;
import com.igse.util.GlobalConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

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
            log.info("Voucher Creation Start...");
            VoucherCodeEntity codeEntity = VoucherCodeEntity.builder()
                    .voucherCode(randomString())
                    .voucherBalance(200.00)
                    .status(GlobalConstant.Voucher.NOT_USED).build();
            VoucherCodeEntity created = codeRepository.save(codeEntity);
            log.info("Voucher Created...{}", created.getVoucherCode());
        }

    }

    /* Should use pagignation*/
    public List<VoucherCodeEntity> getAllVoucher(){
       return codeRepository.findAll();
    }

    private String randomString() {
        return RandomStringUtils.randomAlphanumeric(8).toUpperCase();
    }
}
