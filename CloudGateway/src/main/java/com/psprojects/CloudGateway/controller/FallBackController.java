package com.psprojects.CloudGateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class FallBackController {
    @GetMapping("/orderServiceFallBack")
    public String orderServiceFallBack(){
        return "Order Service is down!";
    }
    @GetMapping("/paymentServiceFallBack")
    public String paymentServiceFallBack(){
        return "Payment Service is down!";
    }
    @GetMapping("/productServiceFallBack")
    public String productServiceFallBack(){
        return "Product Service is down!";
    }

}
