package com.igse.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RegistrationListener {

    @KafkaListener(topics = "v1.dev.regi", groupId = "default-dev")
    public void processRegistration(String data) {
        log.info("Message Received{} ", data);
    }
}
