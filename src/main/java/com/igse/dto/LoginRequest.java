package com.igse.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.igse.dto.user.Login;
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

    @JsonView(value = Login.LoginV2.class)
    @NotNull(message = "UserName should not be empty")
    private String userName;

    @JsonView(value = {Login.LoginV1.class,Login.LoginV2.class})
    @Size(min = 4, max = 20, message = "Password length between 4 to 20")
    private String password;

    @JsonView(value = Login.LoginV1.class)
    @NotNull(message = "Email Id should not be empty",groups = Login.LoginV1.class)
    @Email(message = "Invalid email id")
    private String customerId;

}
