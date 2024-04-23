package com.igse.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "energy_charges")
public class UnitPriceEntity {

    @Id
    private String adminId;
    private Double dayCharge;
    private Double nightCharge;
    private Double gasCharge;
    private Double standingChargePerDay;

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public Double getDayCharge() {
        return dayCharge;
    }

    public void setDayCharge(Double dayCharge) {
        this.dayCharge = dayCharge;
    }

    public Double getNightCharge() {
        return nightCharge;
    }

    public void setNightCharge(Double nightCharge) {
        this.nightCharge = nightCharge;
    }

    public Double getGasCharge() {
        return gasCharge;
    }

    public void setGasCharge(Double gasCharge) {
        this.gasCharge = gasCharge;
    }

    public Double getStandingChargePerDay() {
        return standingChargePerDay;
    }

    public void setStandingChargePerDay(Double standingChargePerDay) {
        this.standingChargePerDay = standingChargePerDay;
    }
}
