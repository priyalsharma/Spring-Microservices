package com.psprojects.ProductService.service;

import com.psprojects.ProductService.model.ProductRequest;
import com.psprojects.ProductService.model.ProductResponse;

public interface ProductService {
    long addProduct(ProductRequest productRequest);
    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}
