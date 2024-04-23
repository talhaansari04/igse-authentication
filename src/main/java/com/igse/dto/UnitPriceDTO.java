package com.igse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class UnitPriceDTO {
    @NotNull(message = "Day charge shouldn't blank")
    @JsonProperty("dayReading")
    private Double dayCharge;

    @NotNull(message = "Night charge shouldn't blank")
    @JsonProperty("nightReading")
    private Double nightCharge;
    @NotNull(message = "Gas charge shouldn't blank")
    @JsonProperty("gasReading")
    private Double gasCharge;
    @NotNull(message = "Standing charge shouldn't blank")
    @JsonProperty("standingChargePerDay")
    private Double standingChargePerDay;


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
