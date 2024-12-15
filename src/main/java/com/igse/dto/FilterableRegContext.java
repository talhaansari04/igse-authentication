package com.igse.dto;

import com.igse.entity.RegistrationStatusEntity;
import lombok.Value;

@Value(staticConstructor = "of")
public class FilterableRegContext {

     String customerId;
     String correlationId;
     String accessToken;
     VoucherResponse voucherResponse;
     RegistrationStatusEntity status;
}
