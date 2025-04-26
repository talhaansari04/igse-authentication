package component.com.igse.test.controller;

import static com.igse.util.IgseConstants.CORRELATION_ID;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igse.controller.LoginAuthentication;
import com.igse.dto.login.LoginRequest;
import com.igse.exception.GlobalExceptionHandler;
import component.com.igse.test.ComponentTestWithStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.UUID;

@ComponentTestWithStub
@Sql(scripts = {"/sql/cleanup_dashboard_admin.sql", "/sql/dashboard_admin.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/cleanup_dashboard_admin.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
class LoginAuthTest {
    private static final String LOGIN_PATH_V1 = "/v1/login";

    @Autowired
    private LoginAuthentication loginAuthentication;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private MockMvc mockMvc;
    HttpHeaders httpHeaders;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        httpHeaders = new HttpHeaders();
        httpHeaders.add(CORRELATION_ID, UUID.randomUUID().toString());
        mockMvc = MockMvcBuilders.standaloneSetup(loginAuthentication)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void loginAuth_loginByCustomerId_success200() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .customerId("talhaansari61@gmail.com")
                .password("root").build();

        mockMvc.perform(post(LOGIN_PATH_V1)
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    void loginAuth_loginByCustomerId_400BadRequest() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .customerId("invalid61@gmail.com")
                .password("invalid").build();

        MvcResult result = mockMvc.perform(post(LOGIN_PATH_V1)
                        .headers(httpHeaders)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.errorDetails[0].message", is("Customer not registered")));
    }
}
