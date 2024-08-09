package com.igse.dto.registration;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
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
@JsonView(value = {RegistrationVersion.V1.class, RegistrationVersion.V2.class})
public class UserRegistrationDTO {
    @Email(message = "Invalid email id")
    private String customerId;

    @NotNull
    private String userName;

    @Size(min = 4, max = 20, message = "Password length between 4 to 20")
    private String pass;


    @Size(min = 8, max = 8, message = "Must be 8 digit")
    private String voucherCode;

    @NotNull(message = " DemographicDetails Should not empty.")
    @Valid
    private DemographicDetails demographicDetails;

}
