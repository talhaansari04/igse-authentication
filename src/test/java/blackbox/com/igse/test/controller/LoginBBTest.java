package blackbox.com.igse.test.controller;

import blackbox.com.igse.test.BlackBoxTest;
import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.report.LevelResolverFactory;
import com.atlassian.oai.validator.restassured.OpenApiValidationFilter;
import com.igse.dto.login.LoginRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.UUID;

import static com.igse.common.IgseConstants.CORRELATION_ID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


@BlackBoxTest
class LoginBBTest {
    private static final String VALID_CUSTOMER_ID = "talhaansari61@gmail.com";
    private static final String BASE_URI = "http://localhost";
    private static final String BASE_PATH = "/igse-auth";
    private static final int PORT = 6000;
    private static final String LOGIN_PATH_V1 = "/v1/login";

    private static final OpenApiValidationFilter OPEN_API_VALIDATION_FILTER=
            new OpenApiValidationFilter(OpenApiInteractionValidator.createFor("src/main/api/loginAuth_v1.swagger.yml")
                    .withBasePathOverride(BASE_PATH)
                    .withLevelResolver(LevelResolverFactory.withAdditionalPropertiesIgnored())
                    .build());
    HttpHeaders httpHeaders;

    @AfterEach
    void tearDown() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("/sql/cleanup_dashboard_admin.sql"));
        }
    }

    @Autowired
    DataSource dataSource;

    @BeforeEach
    void setUp() throws Exception {

        RestAssured.baseURI = BASE_URI;
        RestAssured.basePath = BASE_PATH;
        RestAssured.port = PORT;
        httpHeaders = new HttpHeaders();
        httpHeaders.add(CORRELATION_ID, UUID.randomUUID().toString());

        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("/sql/cleanup_dashboard_admin.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("/sql/dashboard_admin.sql"));
        }
    }

    @Test
    void loginV1_customer_success200() {
        LoginRequest loginRequest = LoginRequest.builder()
                .customerId(VALID_CUSTOMER_ID)
                .password("root").build();

        given()
                .log()
                .all()
                .filter(OPEN_API_VALIDATION_FILTER)
                .headers(httpHeaders)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .post(LOGIN_PATH_V1)
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("customerId", is(VALID_CUSTOMER_ID))
                .body("token", notNullValue())
                .body("wallet.walletId", is("OWARETCNDS"));
    }

    @Test
    void loginV1_customer_unauthorized() {
        LoginRequest loginRequest = LoginRequest.builder()
                .customerId("invalid@gmail.com")
                .password("invalid").build();

        given()
                .log()
                .all()
                .filter(OPEN_API_VALIDATION_FILTER)
                .headers(httpHeaders)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .post(LOGIN_PATH_V1)
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("status", is(404))
                .body("message", is("Customer not registered"));
    }
}