package com.psprojects.OrderService.external.client;

import com.psprojects.OrderService.exception.CustomException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PRODUCT-SERVICE/product")  //SAME AS application.yaml of product service
@CircuitBreaker(name = "external", fallbackMethod =  "fallback")
public interface ProductService {
    @PutMapping("/reduceQuantity/{id}")
    ResponseEntity<Void> reduceQuantity(
            @PathVariable("id") long productId,
            @RequestParam long quantity
    );

    default void fallback(Exception e) {
        throw new CustomException(
                "Product service is not available",
                "UNAVAILABLE",
                500
        );
    }
}
