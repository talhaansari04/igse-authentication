package com.igse.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UnitPriceDTO {
    private String customerId;
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

}
