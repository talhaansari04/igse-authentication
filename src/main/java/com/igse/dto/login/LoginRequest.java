package com.igse.dto.login;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @JsonView(value = LoginVersion.LoginV2.class)
    @NotNull(message = "UserName should not be empty", groups = LoginVersion.LoginV2.class)
    private String userName;

    @JsonView(value = {LoginVersion.LoginV1.class, LoginVersion.LoginV2.class})
    @Size(min = 4, max = 20, message = "Password length between 4 to 20", groups = {LoginVersion.LoginV1.class,
            LoginVersion.LoginV2.class})
    @NotNull(message = "Password should not be empty", groups = {LoginVersion.LoginV1.class,
            LoginVersion.LoginV2.class})
    private String password;

    @JsonView(value = LoginVersion.LoginV1.class)
    @NotNull(message = "Email Id should not be empty", groups = LoginVersion.LoginV1.class)
    @Email(message = "Invalid email id", regexp = ".+@.+\\..+", groups = LoginVersion.LoginV1.class)
    private String customerId;

}
