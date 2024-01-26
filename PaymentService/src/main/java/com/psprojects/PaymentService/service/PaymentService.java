package com.psprojects.PaymentService.service;

import com.psprojects.PaymentService.model.PaymentRequest;
import com.psprojects.PaymentService.model.PaymentResponse;

public interface PaymentService {
    PaymentResponse getPaymentDetailsByOrderId(Long orderId);

    Long doPayment(PaymentRequest paymentRequest);
}
