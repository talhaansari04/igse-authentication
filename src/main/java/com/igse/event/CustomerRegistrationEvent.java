package com.igse.event;

import com.igse.dto.WalletPayloadKafka;
import com.igse.entity.RegistrationStatusEntity;
import com.igse.repository.RegistrationStatusRepo;
import com.igse.util.GlobalConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerRegistrationEvent {
    @Value("${service.kafka.wallet.topic}")
    private String topic;

    private final KafkaTemplate<String, WalletPayloadKafka> kafkaTemplate;

    private final RegistrationStatusRepo statusRepo;

    @Async
    @EventListener
    public void processRegistration(WalletPayloadKafka wallet) {
        Optional<RegistrationStatusEntity> registrationStatus = Optional.empty();
        log.info("message=\"Wallet Event Received of customerId {}...\"", wallet.getCustomerId());
        try {
            CompletableFuture<SendResult<String, WalletPayloadKafka>> future = kafkaTemplate.send(topic, wallet);
            registrationStatus = statusRepo.findByCustomerId(wallet.getCustomerId());
            Optional<RegistrationStatusEntity> finalRegistrationStatus = registrationStatus;
            future.whenComplete((result, ex) -> {

                if (null == ex) {
                    log.info("Message sent successfully ...");
                    finalRegistrationStatus
                            .ifPresent(customer -> {
                                customer.setIsWalletCreated(GlobalConstant.SUCCESS);
                                statusRepo.save(customer);
                            });
                } else {
                    log.error("message=\"Unable to sent message\"");
                    finalRegistrationStatus.ifPresent(this::handleFailedEvent);

                }
            });

        } catch (Exception e) {
            log.error("Kafka event failed ... {}", e.getMessage());
            registrationStatus.ifPresent(this::handleFailedEvent);
        }
    }

    private void handleFailedEvent(RegistrationStatusEntity registrationStatus) {
        registrationStatus.setIsWalletCreated(GlobalConstant.PENDING);
        statusRepo.save(registrationStatus);
    }
}
