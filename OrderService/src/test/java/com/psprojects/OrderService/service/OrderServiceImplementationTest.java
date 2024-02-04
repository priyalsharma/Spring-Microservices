package com.psprojects.OrderService.service;

import com.psprojects.OrderService.exception.CustomException;
import com.psprojects.OrderService.external.client.PaymentService;
import com.psprojects.OrderService.external.client.ProductService;
import com.psprojects.OrderService.external.request.PaymentRequest;
import com.psprojects.OrderService.external.response.PaymentResponse;
import com.psprojects.OrderService.model.OrderRequest;
import com.psprojects.OrderService.model.OrderResponse;
import com.psprojects.OrderService.model.PaymentMode;
import com.psprojects.OrderService.repository.OrderRepository;
import com.psprojects.ProductService.model.ProductResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.psprojects.OrderService.entity.Order;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest // marks as test class
public class OrderServiceImplementationTest {
    @Mock // creates mock object of required actual objects || Note: @Autowired creates and injects actual objects of classes
    private OrderRepository orderRepository;
    @Mock
    private ProductService productService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks //Inject all @Mock in this class
    OrderService orderService = new OrderServiceImplementation();
    @DisplayName("Get - Order - Success Scenario")
    @Test
    void test_When_Order_Success(){
        //MOCKING: Call mocking for all internal calls
        Order order = getMockOrder();
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));
        when(restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/"+order.getProductId(),
                ProductResponse.class
                ))
                .thenReturn(getMockProductResponse());
        when(restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payment/order/"+order.getId(),
                PaymentResponse.class
                ))
                .thenReturn(getMockPaymentResponse());
        //ACTUAL: Actual method of order service called here
        OrderResponse orderResponse = orderService.getOrderDetails(1);
        //ASSERT: assert the operations to check if result is correct or not
        Assertions.assertNotNull(orderResponse);
        Assertions.assertEquals(order.getId(), orderResponse.getOrderID());
        //VERIFICATION: No of service calls, to check the count of no of internal calls--> verifies the internal calls
        verify(orderRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).getForObject(
                "http://PRODUCT-SERVICE/product/"+order.getProductId(),
                ProductResponse.class
        );
        verify(restTemplate, times(1)).getForObject(
                "http://PAYMENT-SERVICE/payment/order/"+order.getId(),
                PaymentResponse.class
        );
    }
    @DisplayName("Get - Order - Failure Scenario")
    @Test
    void test_When_Get_Order_NOT_FOUND_then_Not_Found(){
        //MOCKING
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null)); //means let the repository return null order id of any long value is called
        //ACTUAL: Actual method of order service called here
        CustomException exception =
                assertThrows(CustomException.class,
                        () -> orderService.getOrderDetails(1));
        //ASSERT
        assertEquals("NOT_FOUND",exception.getErrorCode());
        assertEquals(404, exception.getStatus());
        //VERIFY
        verify(orderRepository, times(1))
                .findById(anyLong());
    }
    @DisplayName("Place - Order - Success Scenario")
    @Test
    void test_When_Place_Order_Success(){
        //MOCKING --> check what all this api needs for execution..mock all of that
        OrderRequest orderRequest = getMockOrderRequest();
        Order order = getMockOrder();
        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>( HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<Long>(1L, HttpStatus.OK));
        //ACTUAL
        long orderId = orderService.placeOrder(orderRequest);
        //ASSERT
        assertEquals(order.getId(),orderId);
        //VERIFY
        verify(orderRepository,times(2))
                .save(any());
        verify(productService,times(1))
                .reduceQuantity(anyLong(),anyLong());
        verify(paymentService,times(1))
                .doPayment(any(PaymentRequest.class));
    }
    @DisplayName("Place - Order - Payment Failed Scenario")
    @Test
    /**
     * The API is written such that when order is placed, and payment fails, the order is updated to failed status
     */
    void test_when_Place_Order_Payment_Fails_then_Order_placed(){
        //MOCKING --> check what all this api needs for execution..mock all of that
        OrderRequest orderRequest = getMockOrderRequest();
        Order order = getMockOrder();
        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>( HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());
        //ACTUAL
        long orderId = orderService.placeOrder(orderRequest);
        //ASSERT
        assertEquals(order.getId(),orderId);
        assertEquals(order.getOrderStatus(),"PAYMENT_FAILED");
        //VERIFY
        verify(orderRepository,times(2))
                .save(any());
        verify(productService,times(1))
                .reduceQuantity(anyLong(),anyLong());
        verify(paymentService,times(1))
                .doPayment(any(PaymentRequest.class));
    }
    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .paymentMode(PaymentMode.CASH)
                .productId(1)
                .quantity(10)
                .totalAmount(100)
                .build();
    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .orderId(1)
                .id(1)
                .amount(1100)
                .paymentMode(PaymentMode.CASH)
                .paymentStatus("Success")
                .paymentDate(Instant.now())
                .build();
    }
    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .productId(2)
                .productName("IPhone")
                .price(1100)
                .quantity(700)
                .build();
    }
    private Order getMockOrder() {
        Order order=
                Order.builder()
                        .orderStatus("PLACED")
                        .orderDate(Instant.now())
                        .id(1)
                        .amount(100)
                        .quantity(200)
                        .productId(2)
                        .build();
        return order;
    }
}