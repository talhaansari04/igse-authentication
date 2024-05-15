package com.igse.scheduler;

import com.igse.service.VoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoucherScheduler {
    private final VoucherService voucherService;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void scheduleFixedRateTask() {
        voucherService.createVoucherByScheduler();
    }
}

