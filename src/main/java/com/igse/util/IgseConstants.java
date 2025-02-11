package com.igse.util;

public class IgseConstants {
    public static final String CORRELATION_ID = "X-Correlation-Id";
    public static final String PENDING = "PENDING";
    public static final String SUCCESS = "SUCCESS";
    public static final String BEARER = "Bearer ";
    public static final String USED = "USED";
    public static final String[] REQUEST_MATCHER = new String[]{"/v2/api-docs", "/login", "/register"};
    public static final String GENERAL_EXCEPTION = "GENERAL EXCEPTION";
    public static final String OUT_OF_BOX_TASK_EXECUTOR = "outOfBoxTaskExecutor";
    public static final String DUE = "Due";
    public static final String PAID = "Paid";
    public static final String EURO_SYMBOL = "Â£";
    public static final String UNIT_SYMBOL = "(kWh)";

    private IgseConstants() {
        throw new IllegalArgumentException("IgseConstants.class");
    }

    public static String getCustomerId() {
        return "";
    }

    public static final class Role {
        public static final String USER = "USER";
        public static final String ADMIN = "ADMIN";

        private Role() {
            throw new IllegalArgumentException("Role.class");
        }
    }
}
