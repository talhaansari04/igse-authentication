package com.igse.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
    private UserMaster userMaster;
}
