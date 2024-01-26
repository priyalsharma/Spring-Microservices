package com.psprojects.ProductService.service;

import com.psprojects.ProductService.entity.Product;
import com.psprojects.ProductService.exception.ProductServiceCustomException;
import com.psprojects.ProductService.model.ProductRequest;
import com.psprojects.ProductService.model.ProductResponse;
import com.psprojects.ProductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ProductServiceImplementation implements ProductService{
    @Autowired
    private ProductRepository productRepository;
    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding product");
        Product product = Product.builder()
                .productName(productRequest.getName())
                .quantity(productRequest.getPrice())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
        log.info("Product Created");
        return product.getProductId();
    }
    @Override
    public ProductResponse getProductById(long productId) {
        log.info("Get the product for productId: {}" , productId);
        Product product
                = productRepository.findById(productId)
                .orElseThrow(()->new ProductServiceCustomException("Product with given ID not found","PRODUCT_NOT_FOUND"));
        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product,productResponse);
        log.info(productResponse);
        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce quantity {} for Id: {}",quantity,productId);
        Product product
                = productRepository.findById(productId)
                .orElseThrow(()->new ProductServiceCustomException("" +
                        "Product with given Id not found",
                        "PRODUCT_NOT_FOUND"));
        if(product.getQuantity()<quantity) {
            throw new ProductServiceCustomException(
                    "Product does not have sufficient quantity",
                    "INSUFFICIENT_QUANTITY"
            );
        }
        product.setQuantity(product.getQuantity()-quantity);
        productRepository.save(product);
        log.info("Product Quantity updated successfully");
    }
}
