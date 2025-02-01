package component.com.igse.test.service;

import static com.igse.common.IgseConstants.CORRELATION_ID;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igse.common.IgseConstants;
import com.igse.dto.registration.UserRegRequest;
import com.igse.entity.RegistrationStatusEntity;
import com.igse.repository.db.RegistrationStatusRepo;
import com.igse.service.CustomerService;
import component.com.igse.test.ComponentTestWithStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@ComponentTestWithStub
@Sql(scripts = {"/sql/cleanup_registration_status.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/cleanup_registration_status.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private RegistrationStatusRepo registrationStatusRepo;
    @Autowired
    private ObjectMapper objectMapper;

    static HttpHeaders httpHeaders;

    @BeforeAll
    static void beforeAll() {
        httpHeaders = new HttpHeaders();
        httpHeaders.add(CORRELATION_ID, UUID.randomUUID().toString());
    }

    @Test
    void customer_registrationV1_success() throws Exception {
        UserRegRequest userRegRequest = objectMapper.readValue(REGISTRATION_REQ, UserRegRequest.class);

        assertDoesNotThrow(() -> customerService.saveUser(userRegRequest));
        await()
                .atMost(Duration.ofSeconds(2)).untilAsserted(() -> {
                    Optional<RegistrationStatusEntity> byCustomerId = registrationStatusRepo.findByCustomerId("igseuser61@gmail.com");
                    assertTrue(byCustomerId.isPresent());
                    assertEquals("igseuser61@gmail.com", byCustomerId.get().getCustomerId());

                    assertEquals(IgseConstants.PENDING, byCustomerId.get().getIsVoucherRedeemed());
                    assertEquals(IgseConstants.PENDING, byCustomerId.get().getIsMeterDetailSave());
                    assertEquals(IgseConstants.PENDING, byCustomerId.get().getIsWalletCreated());
                });

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
