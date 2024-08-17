package com.igse.util;

public final class GlobalConstant {
    private GlobalConstant() {
        throw new IllegalArgumentException("GlobalConstant.class");
    }
    public static final String PENDING = "PENDING";
    public static final String SUCCESS = "SUCCESS";
    public static final String BEARER = "Bearer ";

    public static final String USED = "USED";
    public static final String[] REQUEST_MATCHER = {"/v2/api-docs", "/login", "/register"};
    public static final String GENERAL_EXCEPTION = "GENERAL EXCEPTION";
    public static final String OUT_OF_BOX_TASK_EXECUTOR = "outOfBoxTaskExecutor";
    public static final String DUE = "Due";
    public static final String PAID = "Paid";
    public static final String EURO_SYMBOL="£";
    public static final String UNIT_SYMBOL="(kWh)";

    public static String getCustomerId() {
        return "";
    }

    public static final class Role {
        private Role() {
            throw new IllegalArgumentException("Role.class");
        }

        public static final String USER = "USER";
        public static final String ADMIN = "ADMIN";
    }

}
