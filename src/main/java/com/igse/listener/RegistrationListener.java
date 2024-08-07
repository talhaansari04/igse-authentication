package com.igse.listener;

import com.igse.entity.EventLog;
import com.igse.repository.EventLogRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "service.kafka", value = "enable", havingValue = "true")
public class RegistrationListener {

    private final EventLogRepo eventLogRepo;

    @KafkaListener(topics = "v1.dev.regi", groupId = "default-dev")
    public void processRegistration(@Payload String payload, @Header(KafkaHeaders.OFFSET) int offSet, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        log.info("Message Received Payload {} OffSet {} partition {}", payload,offSet,partition);
        eventLogRepo.save(EventLog.builder().eventName(payload).build());
    }
}
