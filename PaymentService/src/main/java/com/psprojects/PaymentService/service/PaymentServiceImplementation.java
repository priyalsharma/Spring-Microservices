package com.psprojects.PaymentService.service;

import com.psprojects.PaymentService.entity.TransactionDetails;
import com.psprojects.PaymentService.model.PaymentMode;
import com.psprojects.PaymentService.model.PaymentRequest;
import com.psprojects.PaymentService.model.PaymentResponse;
import com.psprojects.PaymentService.repository.TransactionDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImplementation implements PaymentService{
    @Autowired
    private TransactionDetailsRepository transactionDetailsRepository;

    @Override
    public PaymentResponse getPaymentDetailsByOrderId(Long orderId) {
        log.info("Getting payment details for orderId {}",orderId);
        TransactionDetails transactionDetails =
                transactionDetailsRepository.findByOrderId(orderId);

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .id(transactionDetails.getId())
                .orderId(transactionDetails.getOrderId())
                .paymentStatus(transactionDetails.getPaymentStatus())
                .paymentDate(transactionDetails.getPaymentDate())
                .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
                .amount(transactionDetails.getAmount())
                .referenceNumber(transactionDetails.getReferenceNumber())
                .build();
        return paymentResponse;
    }

    @Override
    public Long doPayment(PaymentRequest paymentRequest) {
        log.info("Recording payment details");
        TransactionDetails transactionDetails
                = TransactionDetails.builder()
                .paymentDate(Instant.now())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .paymentStatus("SUCCESS")
                .orderId(paymentRequest.getOrderId())
                .referenceNumber(paymentRequest.getReferenceNumber())
                .amount(paymentRequest.getAmount())
                .build();
        transactionDetailsRepository.save(transactionDetails);
        log.info("Transaction completed with ID: "+transactionDetails.getId());
        return transactionDetails.getId();
    }
}
