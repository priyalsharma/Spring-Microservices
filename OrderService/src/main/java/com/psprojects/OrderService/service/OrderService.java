package com.psprojects.OrderService.service;

import com.psprojects.OrderService.model.OrderRequest;
import com.psprojects.OrderService.model.OrderResponse;

public interface OrderService {
    long   placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
