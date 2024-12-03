package blackbox.com.igse.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@BlackBoxTest
class LoginAuthTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void login_admin_success_200(){
        assertNotNull(null);
    }
    @AfterEach
    void tearDown() {
    }
}