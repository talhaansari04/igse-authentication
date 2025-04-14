package component.com.igse.test.controller;

import static com.igse.util.IgseConstants.CORRELATION_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.igse.Utils;
import com.igse.controller.CustomerRegistration;
import com.igse.exception.GlobalExceptionHandler;
import component.com.igse.test.ComponentTestWithStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.UUID;

@ComponentTestWithStub
@Sql(scripts = {"/sql/cleanup_registration_status.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/cleanup_registration_status.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class CustomerRegistrationControllerTest {
    private static final String LOGIN_PATH_V1 = "/v1/register";

    @Autowired
    private CustomerRegistration customerRegistration;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private MockMvc mockMvc;
    HttpHeaders httpHeaders;
    ListAppender<ILoggingEvent> listAppender = Utils.setLogLevel((Logger) LoggerFactory.getLogger(CustomerRegistration.class), new ListAppender<>());

    @BeforeEach
    void setUp() {
        httpHeaders = new HttpHeaders();
        httpHeaders.add(CORRELATION_ID, UUID.randomUUID().toString());
        mockMvc = MockMvcBuilders.standaloneSetup(customerRegistration)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }


    @Test
    void customer_registrationV1_200Success() throws Exception {


        mockMvc.perform(post(LOGIN_PATH_V1)
                        .headers(httpHeaders)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTRATION_REQ)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());


    }

    private final static String REGISTRATION_REQ = """
            {
                "customerId": "igseuser61@gmail.com",
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
