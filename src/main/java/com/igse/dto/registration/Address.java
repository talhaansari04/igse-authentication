package com.igse.dto.registration;

import com.fasterxml.jackson.annotation.JsonView;
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
@JsonView(value = {RegistrationVersion.V1.class,RegistrationVersion.V2.class})
public class Address {
    private String flatNo;
    private String area;
    private String landmark;
    private Long pinCode;
}
