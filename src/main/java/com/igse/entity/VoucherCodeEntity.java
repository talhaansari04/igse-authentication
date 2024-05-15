package com.igse.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
@Data
@Entity
@Builder
@Table(name = "voucher_code")
@NoArgsConstructor
@AllArgsConstructor
public class VoucherCodeEntity {
    @Id
    @Column(unique = true,updatable = false)
    protected String voucherCode;
    protected String customerId;
    protected String status;
    protected Double voucherBalance;
    @UpdateTimestamp
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    protected LocalDate voucherDate;
}
