package com.igse.event;

import static com.igse.common.IgseConstants.CORRELATION_ID;
import static com.igse.common.IgseConstants.SUCCESS;
import com.igse.common.IgseConstants;
import com.igse.dto.WalletPayloadKafka;
import com.igse.entity.RegistrationStatusEntity;
import com.igse.repository.db.RegistrationStatusRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletKafkaProducer {
    @Value("${service.kafka.wallet.topic}")
    private String topic;

    private final KafkaTemplate<String, WalletPayloadKafka> kafkaTemplate;

    private final RegistrationStatusRepo statusRepo;

    // @EventListener
    public void triggerWalletEvent(WalletPayloadKafka wallet, String correlationId) {
        Optional<RegistrationStatusEntity> registrationStatus = Optional.empty();
        log.info("message=\"Wallet Event Received of customerId {}...\"", wallet.getCustomerId());
        try {
            CompletableFuture<SendResult<String, WalletPayloadKafka>> future = kafkaTemplate.send(topic, wallet);
            registrationStatus = statusRepo.findByCustomerId(wallet.getCustomerId());
            Optional<RegistrationStatusEntity> finalRegistrationStatus = registrationStatus;
            future.whenComplete((result, ex) -> {
                MDC.put(CORRELATION_ID, correlationId);
                if (null == ex) {
                    log.info("message=\"Kafka event sent successfully ...");
                    finalRegistrationStatus.ifPresent(this::updateSuccessStatus);
                } else {
                    log.error("message=\"Unable to sent kafka event message\"");
                    finalRegistrationStatus.ifPresent(this::handleFailedEvent);

                }
            });

        } catch (Exception e) {
            log.error("Kafka event failed ... {}", e.getMessage());
            registrationStatus.ifPresent(this::handleFailedEvent);
        }
    }

    private void handleFailedEvent(RegistrationStatusEntity registrationStatus) {
        registrationStatus.setIsWalletCreated(IgseConstants.PENDING);
        statusRepo.save(registrationStatus);
        log.info("message=\"Wallet creation failed {}", registrationStatus.getCustomerId());
    }

    private void updateSuccessStatus(RegistrationStatusEntity customer) {
        statusRepo.updateWalletStatus(customer.getCustomerId(), SUCCESS);
        log.info("message=\"Wallet created successfully for {}", customer);
    }
}
