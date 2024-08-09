package com.igse.dto.registration;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotNull;
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
public class DemographicDetails {

    private Address address;
    private String propertyType;
    @NotNull(message = "flatRegistrationNo should not null",groups = RegistrationVersion.V2.class)
    @JsonView(RegistrationVersion.V2.class)
    private String flatRegistrationNo;
    private Integer numberOfBedRoom;
}
