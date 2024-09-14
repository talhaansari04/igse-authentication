package com.igse.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Entity
@Table(name = "demographic_details")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemographicDetailsEntity {
    @Id
    private String customerId;
    private String  flatRegistrationNo;
    private String propertyType;
    private Integer numberOfBedRoom;
    protected String addressFlatNo;
    protected String addressArea;
    protected String addressLandmark;
    protected Long addressPinCode;
    @OneToOne(mappedBy = "demographicDetails")
    @JsonBackReference
    private UserMaster userMaster;
}
