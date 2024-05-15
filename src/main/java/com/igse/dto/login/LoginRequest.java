package com.igse.dto.login;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @JsonView(value = LoginVersion.LoginV2.class)
    @NotNull(message = "UserName should not be empty")
    private String userName;

    @JsonView(value = {LoginVersion.LoginV1.class,LoginVersion.LoginV2.class})
    @Size(min = 4, max = 20, message = "Password length between 4 to 20")
    private String password;

    @JsonView(value = LoginVersion.LoginV1.class)
    @NotNull(message = "Email Id should not be empty",groups = LoginVersion.LoginV1.class)
    @Email(message = "Invalid email id")
    private String customerId;

}
