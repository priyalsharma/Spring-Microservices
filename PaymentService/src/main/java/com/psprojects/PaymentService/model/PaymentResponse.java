package com.psprojects.PaymentService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private long id;
    private long orderId;
    private long amount;
    private Instant paymentDate;
    private String paymentStatus;
    private String referenceNumber;
    private PaymentMode paymentMode;
}
