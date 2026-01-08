package innowise.java.web_store.service;

import innowise.java.web_store.dto.request.OrderRequest;
import innowise.java.web_store.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse getOrderById(Long id);

    List<OrderResponse> getOrdersByIds(List<Long> ids);

    List<OrderResponse> getOrdersByStatuses(List<String> statuses);

    List<OrderResponse> getOrdersByUserId(String userId);

    OrderResponse createOrder(OrderRequest orderRequest);

    OrderResponse updateOrder(Long id, OrderRequest orderRequest);

    void deleteOrder(Long id);

    void updateOrderStatus(Long id, String status);
}
