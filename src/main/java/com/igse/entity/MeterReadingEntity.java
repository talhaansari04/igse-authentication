package com.igse.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meter_readings")
public class MeterReadingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty("Bill No.")
    protected Integer id;
    @JsonProperty("Customer ID")
    protected String customerId;
    @JsonProperty("Day Reading(in kWh)")
    protected Double dayReading;
    @JsonProperty("Night Reading(in kWh)")
    protected Double nightReading;
    @JsonProperty("Gas Reading(in kWh)")
    protected Double gasReading;
    @JsonProperty("Paid Amount")
    protected Double paidAmount;
    @JsonIgnore
    protected Double dueAmount;
    @JsonProperty("Status")
    protected String billingStatus;
    @JsonProperty("Submit Date")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    protected LocalDate submissionDate;
    @JsonProperty("Payment Date")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    protected LocalDate paymentDate;
}
