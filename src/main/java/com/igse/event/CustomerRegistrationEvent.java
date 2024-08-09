package com.igse.event;

import com.igse.dto.WalletPayloadKafka;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class CustomerRegistrationEvent {
    @Value("${service.kafka.wallet.topic}")
    private String topic;
    @Autowired(required = false)
    private KafkaTemplate<String, WalletPayloadKafka> kafkaTemplate;




    @EventListener
    private void processRegistration(WalletPayloadKafka wallet) {
        log.info("\"Event Received of customerId {}...\"", wallet.getCustomerId());
        try {
            CompletableFuture<SendResult<String, WalletPayloadKafka>> future = kafkaTemplate.send(topic, wallet).completable();
            future.whenComplete((result, ex) -> {
                if (null == ex) {
                    log.info("Message sent successfully ...");
                } else {
                    log.error("Unable to sent message");
                }
            });

        } catch (Exception e) {
            log.error("Kafka event failed ... {}", e.getMessage());
        }
    }
}
