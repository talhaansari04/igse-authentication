package com.igse.scheduler;

import static com.igse.common.IgseConstants.CORRELATION_ID;
import static com.igse.common.IgseConstants.OUT_OF_BOX_TASK_EXECUTOR;
import com.igse.config.MeterConfig;
import com.igse.service.RegistrationEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "poller.outOfBox", value = "enable", havingValue = "true")
public class RegistrationPoller {
    private final RegistrationEventService eventService;
    private final MeterConfig meterConfig;

    @Async(OUT_OF_BOX_TASK_EXECUTOR)
    @Scheduled(cron = "${poller.outOfBox.cron}")
    @SchedulerLock(name = "RoutineScheduler.scheduledTask", lockAtLeastFor = "PT15S", lockAtMostFor = "PT30S")
    public void processPendingRecords() {
        try {
            String correlationId = String.format("%s-%s", getClass().getSimpleName(), UUID.randomUUID());
            MDC.put(CORRELATION_ID, OUT_OF_BOX_TASK_EXECUTOR + "-" + correlationId);

            log.info("message=\"Schedule start\", JobName=\"RegistrationOutBox\"");
            log.info("message=\"MeterPrice {}\", JobName=\"RegistrationOutBox\"", meterConfig);
            eventService.processPendingRecords(correlationId);
        } catch (Exception e) {
            log.error("message=\"Scheduler exception occur\", JobName=\"RegistrationOutBox {}\"", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }


}
