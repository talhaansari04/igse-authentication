package component.com.igse.test.service;

import com.igse.dto.UserResponse;
import com.igse.dto.registration.UserRegRequest;
import com.igse.service.AdminService;
import component.com.igse.test.ComponentTestWithStub;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ComponentTestWithStub
@Sql(scripts = {"/sql/cleanup_dashboard_admin.sql", "/sql/dashboard_admin.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/cleanup_dashboard_admin.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class AdminServiceTest {

    @Autowired
    public AdminService adminService;

    @Captor
    ArgumentCaptor<UserRegRequest> userRegRequestArgumentCaptor;

    @Test
    void admin_Dashboard_Success200() {
        String cid = UUID.randomUUID().toString();
        UserResponse userResponse = adminService.dashBoardData("talhaansari61@gmail.com", cid, cid);
        assertNotNull(userResponse);

        // assertThrows(Exception.class,()->adminService.dashBoardData("test@gmail.com", UUID.randomUUID().toString()));
    }

}
