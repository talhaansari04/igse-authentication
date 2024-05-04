package com.igse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
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


}
