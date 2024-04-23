package com.igse.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class VoucherDTO {

    @Size(min = 8, max = 8,message = "Must be 8 digit and unique")
    protected String voucherCode;
    @NotNull(message = "Voucher amount shouldn't null.")
    protected Double voucherBalance;

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public Double getVoucherBalance() {
        return voucherBalance;
    }

    public void setVoucherBalance(Double voucherBalance) {
        this.voucherBalance = voucherBalance;
    }
}
