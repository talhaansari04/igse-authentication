package com.igse.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserRequest {

    private String userName;

    @Size(min = 4, max = 20, message = "Password length between 4 to 20")
    private String password;
    @NotNull(message = "Email Id should not be empty")
    @Email(message = "Invalid email id")
    private String customerId;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "UserRequest{" +
                "customerId='" + customerId + '\'' +
                '}';
    }
}
