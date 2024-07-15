package com.igse.dto.payment;


import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@ToString
public class IgsePaymentRequest {
    @NotNull(message = "transactionId shouldn't null")
    private String transactionId;
    @NotNull(message = "vendorName shouldn't null")
    private String vendorName;
    @NotNull(message = "amount shouldn't null")
    private double amount;
}
