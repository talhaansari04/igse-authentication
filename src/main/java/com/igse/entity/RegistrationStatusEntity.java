package com.igse.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
    @Column(name = "isWalletCreated")
    private String isWalletCreated;
    @Column(name = "isVoucherRedeemed")
    private String isVoucherRedeemed;
    @Column(name = "isMeterDetailSave")
    private String isMeterDetailSave;
    @Column(columnDefinition = "JSON")
    private String jsonVoucherPayload;
}
