package com.igse.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomerRegistrationEvent {

    @EventListener
    private void processRegistration(String status) {
        log.info("\"message {} data save...\"", status);
    }
}
