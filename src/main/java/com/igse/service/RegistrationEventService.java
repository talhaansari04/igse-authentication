package com.igse.service;

import static com.igse.common.IgseConstants.CORRELATION_ID;
import static com.igse.common.IgseConstants.PAID;
import static com.igse.common.IgseConstants.PENDING;
import static com.igse.common.IgseConstants.SUCCESS;
import static com.igse.common.IgseConstants.USED;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igse.config.MeterConfig;
import com.igse.dto.FilterableRegContext;
import com.igse.dto.MeterReadingDTO;
import com.igse.dto.VoucherResponse;
import com.igse.dto.WalletInfoDTO;
import com.igse.dto.WalletPayloadKafka;
import com.igse.entity.RegistrationStatusEntity;
import com.igse.entity.UserMaster;
import com.igse.event.WalletKafkaProducer;
import com.igse.repository.PaymentRepo;
import com.igse.repository.core.MeterRepo;
import com.igse.repository.core.VoucherRepo;
import com.igse.repository.db.RegistrationStatusRepo;
import com.igse.repository.db.UserMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationEventService {
    private final RegistrationStatusRepo statusRepo;
    //private final ApplicationEventPublisher eventPublisher;
    private final WalletKafkaProducer walletKafkaProducer;
    private final MeterRepo meterRepo;
    private final VoucherRepo voucherRepo;
    private final PaymentRepo paymentRepo;
    private final UserMasterRepository masterRepository;
    private final JwtService jwt;
    private final ObjectMapper objectMapper;
    private final MeterConfig meterConfig;


    public void processPendingRecords(String correlationId) {
        MDC.put(CORRELATION_ID, correlationId);
        List<RegistrationStatusEntity> detail = statusRepo.findRegistrationStatus(PENDING, PENDING, PENDING);
        if (detail.isEmpty()) {
            log.info("message=\"Registration OutOfBox count = 0");
        } else {
            log.info("Event found registration uutOfBox count {}", detail.size());
            detail.forEach(item -> startProcess(item, correlationId));
        }
        MDC.clear();
    }

    private void startProcess(RegistrationStatusEntity status, String correlationId) {
        log.info("message=\"registration process start {}\"", status);
        Optional<UserMaster> userMaster = masterRepository.findById(status.getCustomerId());
        try {
            VoucherResponse voucherResponse = objectMapper.readValue(status.getJsonVoucherPayload(), VoucherResponse.class);
            userMaster.ifPresent(master -> {

                FilterableRegContext regContext = FilterableRegContext.of(
                        master.getCustomerId(),
                        correlationId,
                        jwt.getAdminToken(),
                        voucherResponse,
                        status
                );
                processWallet(regContext);
                processVoucher(regContext);
                processMeterReading(regContext);
            });
        } catch (JsonProcessingException e) {
            log.error("message=\"Unable parse json\", JobName=\"RegistrationOutBox {}\"", e.getMessage());
        }
    }

    private void processWallet(FilterableRegContext regContext) {
        log.info("message=\"wallet process start\"");
        if (PENDING.equalsIgnoreCase(regContext.getStatus().getIsWalletCreated())) {
            WalletPayloadKafka wallet = WalletPayloadKafka.builder()
                    .customerId(regContext.getCustomerId())
                    .totalBalance(regContext.getVoucherResponse().getVoucherBalance())
                    .creationDate(LocalDate.now()).build();
            walletKafkaProducer.triggerWalletEvent(wallet,regContext.getCorrelationId());
            log.info("Wallet event publish successfully {}", regContext.getCustomerId());
        }
    }

    private void processMeterReading(FilterableRegContext regContext) {
        log.info("message=\"Meter process start\"");
        if (PENDING.equalsIgnoreCase(regContext.getStatus().getIsMeterDetailSave())) {
            MeterReadingDTO readingDTO = MeterReadingDTO.builder()
                    .dayReading(meterConfig.getDayReading())
                    .nightReading(meterConfig.getNightReading())
                    .gasReading(meterConfig.getGasReading())
                    .submissionDate(LocalDate.now())
                    .billingStatus(PAID)
                    .customerId(regContext.getCustomerId()).build();
            meterRepo.saveMeterDetails(readingDTO,regContext.getCorrelationId());
            statusRepo.updateMeterDetailStatus(regContext.getCustomerId(), SUCCESS);
            log.info("Meter reading save successfully {}", regContext.getCustomerId());
        }
    }

    private void processVoucher(FilterableRegContext regContext) {
        log.info("message=\"Voucher verification process start\"");
        VoucherResponse voucherDetails = regContext.getVoucherResponse();

        if (PENDING.equalsIgnoreCase(regContext.getStatus().getIsVoucherRedeemed())) {
           try{
               WalletInfoDTO walletDetails = paymentRepo.walletDetails(regContext.getCustomerId(),
                       regContext.getAccessToken(), regContext.getCorrelationId());
                log.info("message=\"Wallet has been created {}",regContext.getCustomerId());
               if (null != walletDetails) {
                   VoucherResponse voucherCode = VoucherResponse.builder()
                           .voucherCode(voucherDetails.getVoucherCode())
                           .status(USED)
                           .customerId(regContext.getCustomerId())
                           .voucherBalance(voucherDetails.getVoucherBalance()).build();

                   voucherRepo.saveSingleDetail(voucherCode,regContext.getCorrelationId());
                   log.info("Voucher details save successfully {}", regContext.getCustomerId());
                   statusRepo.updateVoucherRedeemedStatus(regContext.getCustomerId(), SUCCESS);
               }
           }
           catch (Exception e){
            log.error("message=\"Wallet not found {} ",e.getMessage());
           }
        }
    }
}
