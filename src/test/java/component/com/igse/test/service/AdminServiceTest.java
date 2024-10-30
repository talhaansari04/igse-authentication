package component.com.igse.test.service;

import com.igse.dto.UserResponse;
import com.igse.service.AdminService;
import component.com.igse.test.ComponentTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ComponentTest
@Sql(scripts = { "/init-database.sql", "/populate-data.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class AdminServiceTest {

    @Autowired
    public AdminService adminService;

    @Test
    void admin_Dashboard_Success200(){
        UserResponse userResponse = adminService.dashBoardData("test-123", UUID.randomUUID().toString());
        assertNotNull(userResponse);
    }

}
