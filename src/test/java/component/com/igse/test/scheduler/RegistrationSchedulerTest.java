package component.com.igse.test.scheduler;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import com.igse.common.IgseConstants;
import com.igse.entity.RegistrationStatusEntity;
import com.igse.repository.db.RegistrationStatusRepo;
import com.igse.service.RegistrationEventService;
import component.com.igse.test.ComponentTestWithStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import java.time.Duration;
import java.util.Optional;

@ComponentTestWithStub
@TestPropertySource(properties = {"poller.outOfBox.enable=true", "poller.outOfBox.cron=0/5 * * * * *"})
@Sql(scripts = {"/sql/cleanup_registration_status.sql", "/sql/registration_status.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/cleanup_registration_status.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class RegistrationSchedulerTest {


    @Autowired
    private RegistrationStatusRepo registrationRepo;

    @SpyBean
    private RegistrationEventService eventService;

    long countBefore;

    @BeforeEach
    void setUp() {
        countBefore = registrationRepo.count();
    }

    @Test
    void registration_scheduler_verifier() {
        assertEquals(1, registrationRepo.count());
        await()
                .atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
                    verify(eventService, atLeastOnce()).processPendingRecords(anyString());
                    Optional<RegistrationStatusEntity> byCustomerId = registrationRepo.findByCustomerId("igseuser61@gmail.com");
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
