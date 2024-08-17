package com.igse.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.igse.dto.MeterReadingDTO;
import com.igse.dto.VoucherResponse;
import com.igse.dto.WalletInfoDTO;
import com.igse.dto.WalletPayloadKafka;
import com.igse.entity.RegistrationStatusEntity;
import com.igse.entity.UserMaster;
import com.igse.repository.PaymentRepo;
import com.igse.repository.RegistrationStatusRepo;
import com.igse.repository.UserMasterRepository;
import com.igse.repository.core.MeterRepo;
import com.igse.repository.core.VoucherRepo;
import com.igse.service.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.igse.util.GlobalConstant.OUT_OF_BOX_TASK_EXECUTOR;
import static com.igse.util.GlobalConstant.PAID;
import static com.igse.util.GlobalConstant.PENDING;
import static com.igse.util.GlobalConstant.SUCCESS;
import static com.igse.util.GlobalConstant.USED;


@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationPoller {
    private final RegistrationStatusRepo statusRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final MeterRepo meterRepo;
    private final VoucherRepo voucherRepo;
    private final PaymentRepo paymentRepo;
    private final UserMasterRepository masterRepository;
    private final KafkaTemplate<String, WalletPayloadKafka> kafkaTemplate;
    private final JwtService jwt;


    @Async(OUT_OF_BOX_TASK_EXECUTOR)
    @Scheduled(cron = "0 0/1 * * * ?")
    public void processPendingRecords() {
        log.info("*****Poller Service*****");
        List<RegistrationStatusEntity> detail = statusRepo.findRegistrationStatus(PENDING, PENDING, PENDING);
        if (detail.isEmpty()) {
            log.info("Registration OutOfBox count = 0");
        } else {
            log.info("Event found*****");
            log.info("Registration OutOfBox count {}", detail.size());
            detail.forEach(this::startProcess);
        }
    }

    @SneakyThrows
    private void startProcess(RegistrationStatusEntity status) {
        Optional<UserMaster> userMaster = masterRepository.findById(status.getCustomerId());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        VoucherResponse voucherResponse = objectMapper.readValue(status.getJsonVoucherPayload(), VoucherResponse.class);
        userMaster.ifPresent(master -> {
            processWallet(master, voucherResponse, status);
            processVoucher(master.getCustomerId(), status, voucherResponse);
            processMeterReading(master, status);
        });
    }

    @Transactional
    private void processWallet(UserMaster master, VoucherResponse voucherResponse, RegistrationStatusEntity status) {
        if (PENDING.equalsIgnoreCase(status.getIsWalletCreated())) {
            publishWalletEvent(master, voucherResponse);

        }
    }

    @Transactional
    private void processMeterReading(UserMaster master, RegistrationStatusEntity status) {
        if (PENDING.equalsIgnoreCase(status.getIsMeterDetailSave())) {
            setMeterReadingInitialValue(master.getCustomerId());
            status.setIsMeterDetailSave(SUCCESS);
            statusRepo.save(status);
        }
    }

    @Transactional
    private void processVoucher(String customerId, RegistrationStatusEntity status, VoucherResponse voucherResponse) {
        if (PENDING.equalsIgnoreCase(status.getIsVoucherRedeemed())) {
            WalletInfoDTO walletDetails = paymentRepo.walletDetails(customerId, jwt.getAdminToken());
            if (null != walletDetails) {
                saveVoucher(customerId, voucherResponse);
                status.setIsVoucherRedeemed(SUCCESS);
                statusRepo.save(status);
            }
        }
    }

    private void publishWalletEvent(UserMaster userMaster, VoucherResponse voucherDetails) {
        WalletPayloadKafka wallet = WalletPayloadKafka.builder()
                .customerId(userMaster.getCustomerId())
                .totalBalance(voucherDetails.getVoucherBalance())
                .creationDate(LocalDate.now()).build();
        eventPublisher.publishEvent(wallet);
        log.info("Wallet event publish successfully {}", userMaster.getCustomerId());
    }

    private void setMeterReadingInitialValue(String customerId) {
        MeterReadingDTO readingDTO = MeterReadingDTO.builder()
                .dayReading(100.00)
                .nightReading(250.0)
                .gasReading(800.00)
                .submissionDate(LocalDate.now())
                .billingStatus(PAID)
                .customerId(customerId).build();
        meterRepo.saveMeterDetails(readingDTO);
        log.info("Meter reading save successfully {}", customerId);
    }

    private void saveVoucher(String customerId, VoucherResponse voucherDetails) {
        VoucherResponse voucherCode = VoucherResponse.builder()
                .voucherCode(voucherDetails.getVoucherCode())
                .status(USED)
                .customerId(customerId)
                .voucherBalance(voucherDetails.getVoucherBalance()).build();
        voucherRepo.saveSingleDetail(voucherCode);
        log.info("Voucher details save successfully {}", customerId);
    }
}
