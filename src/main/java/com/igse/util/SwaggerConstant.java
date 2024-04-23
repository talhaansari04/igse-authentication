package com.igse.util;

public final class SwaggerConstant {
    private SwaggerConstant() {
        throw new IllegalArgumentException("SwaggerSetting.class");
    }

    public static final String NAME = "iGSE";
    public static final String API_TITLE = "Shangri-La's Energy Bills Support Scheme";
    public static final String API_DESC = "This is Swagger.2 for Rest API Test";
    public static final String LICENSE = "iGSE";
    public static final String TERMS_OF_SERVICE_URL = "";
    public static final String LICENSE_URL = "";
    public static final String VERSION = "2.0";


    public static final String SWAGGER_UI_URL = "swagger-ui.html";
    public static final String SWAGGER_RESOURCE = "classpath:/META-INF/resources/";
    public static final String SWAGGER_JAR = "/webjars/**";
    public static final String SWAGGER_LOCATION = "classpath:/META-INF/resources/webjars/";
}
