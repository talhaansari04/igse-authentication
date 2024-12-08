package component.com.igse.test.controller;

import com.igse.controller.LoginAuthentication;
import com.igse.dto.UserResponse;
import com.igse.dto.login.LoginRequest;
import component.com.igse.test.ComponentTestWithStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.Callable;

@ComponentTestWithStub
@Sql(scripts = {"/sql/cleanup_dashboard_admin.sql", "/sql/dashboard_admin.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/cleanup_dashboard_admin.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class LoginAuthTest {

    @Autowired
    private LoginAuthentication loginAuthentication;

    @BeforeEach
    void setUp() {

    }

    @Test

    void loginAuth_login_success() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .userName("talhaansari61@gmail.com")
                .password("root").build();
        Callable<UserResponse> userResponseCallable = loginAuthentication.loginAuthV1(loginRequest);
       // System.out.println(userResponseCallable.call().getToken());
    }
}
