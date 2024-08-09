package com.igse.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "igse_registration_status")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String customerId;
    @Column(columnDefinition = "JSON")
    private String isWalletCreated;
    @Column(name = "isVoucherRedeemed")
    private String isVoucherRedeemed;
    @Column(name = "isMeterDetailSave")
    private String isMeterDetailSave;
    @Column(columnDefinition = "JSON")
    private String jsonVoucherPayload;
}
