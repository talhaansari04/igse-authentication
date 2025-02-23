package blackbox.com.igse.test.controller;

import static com.igse.common.IgseConstants.CORRELATION_ID;
import static io.restassured.RestAssured.given;
import blackbox.com.igse.test.BlackBoxTest;
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

@BlackBoxTest
class CustomerRegistrationBBTest {
    private static final String VALID_CUSTOMER_ID = "talhaansari61@gmail.com";
    private static final String BASE_URI = "http://localhost";
    private static final String BASE_PATH = "/igse-auth";
    private static final int PORT = 6000;
    private static final String REGISTRATION_PATH_V1 = "v2/register";
    HttpHeaders httpHeaders;

    @AfterEach
    void tearDown() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("/sql/cleanup_registration_status.sql"));
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


        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("/sql/cleanup_registration_status.sql"));
        }
    }

    @Test
    void customer_registrationV1_201success() {
        httpHeaders.add(CORRELATION_ID, UUID.randomUUID().toString());
        given()
                .log()
                .all()
                .headers(httpHeaders)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(REGISTRATION_REQ)
                .post(REGISTRATION_PATH_V1)
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_CREATED);
/*                .body("customerId", is(VALID_CUSTOMER_ID))
                .body("token", notNullValue())
                .body("wallet.walletId", is("OWARETCNDS"));*/

    }

    private final static String REGISTRATION_REQ = """
            {
                "customerId": "talhaansari61@gmail.com",
                "pass": "root",
                "userName": "igse",
                "voucherCode": "62ANW9MV",
                "demographicDetails": {
                    "flatRegistrationNo": "AZ-985765",
                    "numberOfBedRoom": 1,
                    "propertyType": "Single",
                    "address": {
                        "flatNo": "129",
                        "area": "Okhla",
                        "landmark": "Jamia",
                        "pinCode": 110025
                    }
                }
            }
            """;
}
