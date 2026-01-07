package innowise.java.web_store.service.impl;

import innowise.java.web_store.client.UserClient;
import innowise.java.web_store.dto.request.OrderRequest;
import innowise.java.web_store.dto.response.OrderResponse;
import innowise.java.web_store.dto.response.UserResponse;
import innowise.java.web_store.entity.Item;
import innowise.java.web_store.entity.Order;
import innowise.java.web_store.entity.OrderItem;
import innowise.java.web_store.exception.ApiException;
import innowise.java.web_store.exception.ApiExceptionType;
import innowise.java.web_store.mapper.OrderMapper;
import innowise.java.web_store.repository.ItemRepository;
import innowise.java.web_store.repository.OrderRepository;
import innowise.java.web_store.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final UserClient userClient;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "orders", key = "#id")
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new ApiException(ApiExceptionType.ERR_NOT_FOUND));
        UserResponse user = userClient.getUserByEmail(order.getUserId());
        OrderResponse response = orderMapper.toResponse(order);
        response.setUser(user);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByIds(List<Long> ids) {
        List<Order> orders = orderRepository.findAllByIdIn(ids);
        return orders.stream().map(order -> {
            UserResponse user = userClient.getUserByEmail(order.getUserId());
            OrderResponse response = orderMapper.toResponse(order);
            response.setUser(user);
            return response;
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatuses(List<String> statuses) {
        List<Order> orders = orderRepository.findAllByStatusIn(statuses);
        return orders.stream().map(order -> {
            UserResponse user = userClient.getUserByEmail(order.getUserId());
            OrderResponse response = orderMapper.toResponse(order);
            response.setUser(user);
            return response;
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(String userId) {
        List<Order> orders = orderRepository.findAllByUserId(userId);
        return orders.stream().map(order -> {
            UserResponse user = userClient.getUserByEmail(order.getUserId());
            OrderResponse response = orderMapper.toResponse(order);
            response.setUser(user);
            return response;
        }).toList();
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = orderMapper.toEntity(orderRequest);
        Order savedOrder = orderRepository.save(order);

        for (OrderItem oi : order.getOrderItems()) {
            Item dbItem = itemRepository.findById(oi.getItem().getId())
                    .orElseThrow(() -> new ApiException(ApiExceptionType.ERR_NOT_FOUND));
            oi.setItem(dbItem);
            oi.setOrder(order);
        }

        UserResponse user = userClient.getUserByEmail(savedOrder.getUserId());
        OrderResponse response = orderMapper.toResponse(savedOrder);
        response.setUser(user);
        return response;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "orders", key = "#id"),
    })
    public OrderResponse updateOrder(Long id, OrderRequest orderRequest) {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new ApiException(ApiExceptionType.ERR_NOT_FOUND));
        orderMapper.updateEntityFromRequest(orderRequest, order);
        Order updatedOrder = orderRepository.save(order);

        UserResponse user = userClient.getUserByEmail(updatedOrder.getUserId());
        OrderResponse response = orderMapper.toResponse(updatedOrder);
        response.setUser(user);
        return response;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "orders", key = "#id"),
    })
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ApiException(ApiExceptionType.ERR_NOT_FOUND);
        }
        orderRepository.deleteById(id);
    }
}
