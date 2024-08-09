package com.igse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDate;

@Data
@Entity
@Table(name = "igse_user_master")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMaster {

	@Id
	private String customerId;
	private String role;
	private String userName;
	@JsonIgnore
	private String pass;

	@JsonIgnore
	@UpdateTimestamp
	private LocalDate lastLogin;

    @OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "customerId")
	private DemographicDetailsEntity demographicDetails;
}
