package com.igse.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class WalletPayloadKafka {
    private String customerId;
    private Double totalBalance;
    private LocalDate creationDate;
}
