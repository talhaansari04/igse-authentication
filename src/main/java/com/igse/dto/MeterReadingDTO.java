package com.igse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class MeterReadingDTO {
    protected Integer id;
    @NotNull(message = "Day shouldn't null")
    protected Double dayReading;
    @NotNull(message = "Night shouldn't null")
    protected Double nightReading;
    @NotNull(message = " Gas shouldn't null")
    protected Double gasReading;

    protected Double amount;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    protected LocalDate submissionDate;

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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
