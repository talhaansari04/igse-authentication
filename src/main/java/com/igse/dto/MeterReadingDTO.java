package com.igse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterReadingDTO {
    protected Integer id;
    protected String customerId;
    private String billingStatus;
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
