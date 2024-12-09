package blackbox.com.igse.test.controller;

import blackbox.com.igse.test.BlackBoxTest;
import com.igse.dto.login.LoginRequest;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
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

import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.not;


@BlackBoxTest
class UserRegistrationTest {
    private static final String BASE_URI = "http://localhost";
    private static final String BASE_PATH = "/igse/auth";
    private static final int PORT = 6000;
    private static final String REGISTRATION_PATH_V1 = "/v1/login";
    static HttpHeaders httpHeaders;

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
        httpHeaders.add("X-Correlation-Id", UUID.randomUUID().toString());

        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("/sql/cleanup_dashboard_admin.sql"));
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("/sql/dashboard_admin.sql"));
        }
    }

    @Test
    void customerQuerySuccess() {
        LoginRequest loginRequest = LoginRequest.builder()
                .customerId("talhaansari611@gmail.com")
                .password("root").build();
        given()
                .log()
                .all()
                .headers(httpHeaders)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(loginRequest)
                .post(REGISTRATION_PATH_V1)
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body(not(Matchers.empty()));
    }
}
