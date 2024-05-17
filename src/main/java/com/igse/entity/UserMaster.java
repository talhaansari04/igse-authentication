package com.igse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
	private String address;
	private String propertyType;
	private Integer numberOfBedRoom;
	private Double currentBalance;
	@JsonIgnore
	@UpdateTimestamp
	private LocalDate lastLogin;
}
