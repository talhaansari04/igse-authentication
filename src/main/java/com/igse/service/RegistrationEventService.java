package com.igse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igse.dto.MeterReadingDTO;
import com.igse.dto.VoucherResponse;
import com.igse.dto.WalletInfoDTO;
import com.igse.dto.WalletPayloadKafka;
import com.igse.entity.RegistrationStatusEntity;
import com.igse.entity.UserMaster;
import com.igse.repository.PaymentRepo;
import com.igse.repository.core.MeterRepo;
import com.igse.repository.core.VoucherRepo;
import com.igse.repository.db.RegistrationStatusRepo;
import com.igse.repository.db.UserMasterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.igse.common.IgseConstants.CORRELATION_ID;
import static com.igse.common.IgseConstants.PAID;
import static com.igse.common.IgseConstants.PENDING;
import static com.igse.common.IgseConstants.SUCCESS;
import static com.igse.common.IgseConstants.USED;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationEventService {
    private final RegistrationStatusRepo statusRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final MeterRepo meterRepo;
    private final VoucherRepo voucherRepo;
    private final PaymentRepo paymentRepo;
    private final UserMasterRepository masterRepository;
    private final JwtService jwt;
    private final ObjectMapper objectMapper;


    public void processPendingRecords(String correlationId) {
        MDC.put(CORRELATION_ID, correlationId);
        log.info("*****Poller Service*****");
        List<RegistrationStatusEntity> detail = statusRepo.findRegistrationStatus(PENDING, PENDING, PENDING);
        if (detail.isEmpty()) {
            log.info("Registration OutOfBox count = 0");
        } else {
            log.info("Event found registration uutOfBox count {}", detail.size());
            detail.forEach(this::startProcess);
        }
        MDC.clear();
    }

    private void startProcess(RegistrationStatusEntity status) {
        log.info("message=\"registration process start {}\"",status);
        Optional<UserMaster> userMaster = masterRepository.findById(status.getCustomerId());
        try {
            VoucherResponse voucherResponse = objectMapper.readValue(status.getJsonVoucherPayload(), VoucherResponse.class);
            userMaster.ifPresent(master -> {
                processWallet(master, voucherResponse, status);
                processVoucher(master.getCustomerId(), status, voucherResponse);
                processMeterReading(master, status);
            });
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    @Transactional
    private void processWallet(UserMaster master, VoucherResponse voucherResponse, RegistrationStatusEntity status) {
        log.info("message=\"wallet process start\"");
        if (PENDING.equalsIgnoreCase(status.getIsWalletCreated())) {
            publishWalletEvent(master, voucherResponse);

        }
    }

    @Transactional
    private void processMeterReading(UserMaster master, RegistrationStatusEntity status) {
        log.info("message=\"Meter process start\"");
        if (PENDING.equalsIgnoreCase(status.getIsMeterDetailSave())) {
            setMeterReadingInitialValue(master.getCustomerId());
            status.setIsMeterDetailSave(SUCCESS);
            statusRepo.save(status);
        }
    }

    @Transactional
    private void processVoucher(String customerId, RegistrationStatusEntity status, VoucherResponse voucherResponse) {
        log.info("message=\"Voucher verification process start\"");
        if (PENDING.equalsIgnoreCase(status.getIsVoucherRedeemed())) {
            WalletInfoDTO walletDetails = paymentRepo.walletDetails(customerId, jwt.getAdminToken(), UUID.randomUUID().toString());
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
