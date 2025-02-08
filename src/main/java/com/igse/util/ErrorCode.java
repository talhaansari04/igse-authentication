package com.igse.util;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND("1001", "Customer not registered"),
    USER_ALREADY_EXIST("1002", "Customer already exist"),
    INVALID_CREDENTIAL("1003", "Invalid Credential"),
    USER_INFO_FOUND("1004", "User data not found"),
    EVC_COUPON_USED("1005", "EVC code already used"),
    EVC_COUPON_INVALID("1006", "Invalid EVC code"),
    WALLET_NOT_FOUND("1007", "Wallet not found");

    private static final String PRE_FIX = "IGSE_AUTH-";
    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getErrorCode() {
        return PRE_FIX + code;
    }
}
