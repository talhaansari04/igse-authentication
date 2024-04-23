package com.igse.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class UserRegistrationDTO {
    @Email(message = "Invalid email id")
    private String customerId;
    private String userName;
    @Size(min = 4, max = 20,message = "Password length between 4 to 20")
    private String pass;
    private String address;
    private String propertyType;
    private Integer numberOfBedRoom;

    @Size(min = 8, max = 8,message = "Must be 8 digit")
    private String voucherCode;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public Integer getNumberOfBedRoom() {
        return numberOfBedRoom;
    }

    public void setNumberOfBedRoom(Integer numberOfBedRoom) {
        this.numberOfBedRoom = numberOfBedRoom;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }
}
