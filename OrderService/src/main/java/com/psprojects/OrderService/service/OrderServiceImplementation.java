package com.psprojects.OrderService.service;

import com.psprojects.OrderService.entity.Order;
import com.psprojects.OrderService.exception.CustomException;
import com.psprojects.OrderService.external.client.PaymentService;
import com.psprojects.OrderService.external.client.ProductService;
import com.psprojects.OrderService.external.request.PaymentRequest;
import com.psprojects.OrderService.external.response.PaymentResponse;
import com.psprojects.OrderService.model.OrderRequest;
import com.psprojects.OrderService.model.OrderResponse;
import com.psprojects.OrderService.repository.OrderRepository;
import com.psprojects.ProductService.model.ProductResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImplementation implements OrderService{
   @Autowired
    private OrderRepository orderRepository;
   @Autowired
    private ProductService productService;
   @Autowired
   private PaymentService paymentService;
   @Autowired
   private RestTemplate restTemplate;
    @Override
    public long placeOrder(OrderRequest orderRequest) {
        //Order Entity-> Save the data with Status Order Created
        //Call product service - block products (Reduce the quantity)
        //Call Payment service to complete payment
            //If payment success -> COMPLETE
            //else CANCELLED
        log.info("Placing order request: {}", orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity()); //directly call the product service--> REST API call using Feign client

        log.info("Creating Order with Status CREATED");
        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();
        order = orderRepository.save(order);
        log.info("Calling payment service to complete the payment");
        PaymentRequest paymentRequest
                = PaymentRequest.builder()
                                .orderId(order.getId())
                                .paymentMode(orderRequest.getPaymentMode())
                                .amount(orderRequest.getTotalAmount())
                                .build();
        String orderStatus = null;
        try{
            paymentService.doPayment(paymentRequest);
            log.info("Payment done successfully. Changing the order status to PLACED");
            orderStatus = "PLACED";
        }
        catch( Exception e){
            log.error("Error occurred in payment. Changing order status to FAILED");
            orderStatus = "PAYMENT_FAILED";
        }
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        log.info("Order placed successfully with Order Id: {}",order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order details for order Id: "+orderId);
        Order order
                = orderRepository.findById(orderId)
                .orElseThrow(()-> new CustomException("Order not found for the orderId "+orderId
                ,"NOT_FOUND"
                ,404));
        log.info("Invoking Product service to fetch product for id:{} ", order.getId());
        ProductResponse productResponse
                = restTemplate.getForObject(
                        "http://PRODUCT-SERVICE/product/"+order.getProductId(),
                ProductResponse.class
        );
        log.info("Invoking Payment service to fetch payment details for id:{} ", order.getId());
        PaymentResponse paymentResponse
                = restTemplate.getForObject(
                        "http://PAYMENT-SERVICE/payment/order/"+order.getId(),
                PaymentResponse.class
        );
        OrderResponse.ProductDetails productDetails
                = OrderResponse.ProductDetails
                        .builder()
                        .productId(productResponse.getProductId())
                        .productName(productResponse.getProductName())
                        .quantity(productResponse.getQuantity())
                        .price(productResponse.getPrice())
                        .build();
        OrderResponse.PaymentDetails paymentDetails
                = OrderResponse.PaymentDetails
                .builder()
                .id(paymentResponse.getId())
                .orderId(paymentResponse.getOrderId())
                .amount(paymentResponse.getAmount())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentStatus(paymentResponse.getPaymentStatus())
                .referenceNumber(paymentResponse.getReferenceNumber())
                .paymentMode(paymentResponse.getPaymentMode())
                .build();

        OrderResponse orderResponse
                = OrderResponse.builder()
                .orderID((order.getId()))
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();

        return orderResponse;
    }
}
