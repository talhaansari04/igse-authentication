package com.igse.event;

import com.igse.dto.VoucherResponse;
import com.igse.dto.WalletPayloadKafka;
import com.igse.entity.UserMaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerRegistrationEvent {
    @Value("${service.kafka.wallet.topic}")
    private String topic;

    private final KafkaTemplate<String, WalletPayloadKafka> kafkaTemplate;

    @EventListener
    private void processRegistration(WalletPayloadKafka wallet) {
        log.info("\"Event Received of customerId {}...\"", wallet.getCustomerId());
        ListenableFuture<SendResult<String, WalletPayloadKafka>> future = kafkaTemplate.send(topic, wallet);
    }
}
