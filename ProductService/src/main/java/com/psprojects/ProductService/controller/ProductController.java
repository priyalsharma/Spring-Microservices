package com.psprojects.ProductService.controller;

import com.psprojects.ProductService.model.ProductRequest;
import com.psprojects.ProductService.model.ProductResponse;
import com.psprojects.ProductService.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product")
@Log4j2
public class ProductController {
    @Autowired
    private ProductService productService;
    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping
    public ResponseEntity<Long> addProduct(@RequestBody ProductRequest productRequest){
        long productId = productService.addProduct(productRequest);
        return new ResponseEntity<>(productId, HttpStatus.CREATED);
    }
    @PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer') || hasAuthority('SCOPE_internal')")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable long productId){
        log.info("Fetching product with ID "+productId);
        ProductResponse productResponse
                = productService.getProductById(productId);
        log.info("Completed product "+productId);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }
    @PutMapping("/reduceQuantity/{id}")
    public ResponseEntity<Void> reduceQuantity(
            @PathVariable("id") long productId,
            @RequestParam long quantity
    ){
        productService.reduceQuantity(productId,quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
