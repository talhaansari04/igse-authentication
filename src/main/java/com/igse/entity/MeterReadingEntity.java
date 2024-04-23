package com.igse.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Double getDayReading() {
        return dayReading;
    }

    public void setDayReading(Double dayReading) {
        this.dayReading = dayReading;
    }

    public Double getNightReading() {
        return nightReading;
    }

    public void setNightReading(Double nightReading) {
        this.nightReading = nightReading;
    }

    public Double getGasReading() {
        return gasReading;
    }

    public void setGasReading(Double gasReading) {
        this.gasReading = gasReading;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getBillingStatus() {
        return billingStatus;
    }

    public void setBillingStatus(String billingStatus) {
        this.billingStatus = billingStatus;
    }

    public Double getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(Double dueAmount) {
        this.dueAmount = dueAmount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
}
