package com.igse.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {

    @Size(min = 8, max = 8,message = "Must be 8 digit and unique")
    protected String voucherCode;
    @NotNull(message = "Voucher amount shouldn't null.")
    protected Double voucherBalance;

}
