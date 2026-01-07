package innowise.java.web_store.service;

import innowise.java.web_store.client.UserClient;
import innowise.java.web_store.dto.request.OrderRequest;
import innowise.java.web_store.dto.response.OrderResponse;
import innowise.java.web_store.dto.response.UserResponse;
import innowise.java.web_store.entity.Order;
import innowise.java.web_store.mapper.OrderMapper;
import innowise.java.web_store.repository.OrderRepository;
import innowise.java.web_store.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderRequest orderRequest;
    private OrderResponse orderResponse;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        order = new Order();
        order.setId(1L);
        order.setUserId("mail@mail.mail");

        orderRequest = new OrderRequest();
        orderRequest.setUserId("mail@mail.mail");

        userResponse = new UserResponse();
        userResponse.setEmail("mail@mail.mail");

        orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setUser(userResponse);
    }

    @Test
    void testGetOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        when(userClient.getUserByEmail("mail@mail.mail")).thenReturn(userResponse);

        OrderResponse response = orderService.getOrderById(1L);

        assertNotNull(response.getUser());
        assertEquals("mail@mail.mail", response.getUser().getEmail());
        verify(orderRepository).findById(1L);
        verify(userClient).getUserByEmail("mail@mail.mail");
    }

    @Test
    void testGetOrdersByIds() {
        List<Long> ids = List.of(1L, 2L);
        Order order2 = new Order();
        order2.setId(2L);
        order2.setUserId("mail2@mail.mail");

        UserResponse user2 = new UserResponse();
        user2.setEmail("mail2@mail.mail");

        OrderResponse response2 = new OrderResponse();
        response2.setId(2L);
        response2.setUser(user2);

        when(orderRepository.findAllByIdIn(ids)).thenReturn(List.of(order, order2));
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        when(orderMapper.toResponse(order2)).thenReturn(response2);
        when(userClient.getUserByEmail("mail@mail.mail")).thenReturn(userResponse);
        when(userClient.getUserByEmail("mail2@mail.mail")).thenReturn(user2);

        List<OrderResponse> responses = orderService.getOrdersByIds(ids);
        assertEquals(2, responses.size());
        assertEquals("mail@mail.mail", responses.get(0).getUser().getEmail());
        assertEquals("mail2@mail.mail", responses.get(1).getUser().getEmail());
    }

    @Test
    void testGetOrdersByStatuses() {
        order.setStatus("NEW");
        when(orderRepository.findAllByStatusIn(List.of("NEW"))).thenReturn(List.of(order));
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        when(userClient.getUserByEmail("mail@mail.mail")).thenReturn(userResponse);

        List<OrderResponse> responses = orderService.getOrdersByStatuses(List.of("NEW"));
        assertEquals(1, responses.size());
        assertEquals("mail@mail.mail", responses.get(0).getUser().getEmail());
    }

    @Test
    void testGetOrdersByUserId() {
        when(orderRepository.findAllByUserId("mail@mail.mail")).thenReturn(List.of(order));
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        when(userClient.getUserByEmail("mail@mail.mail")).thenReturn(userResponse);

        List<OrderResponse> responses = orderService.getOrdersByUserId("mail@mail.mail");
        assertEquals(1, responses.size());
        assertEquals("mail@mail.mail", responses.get(0).getUser().getEmail());
    }

    @Test
    void testCreateOrder() {
        when(orderMapper.toEntity(orderRequest)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        when(userClient.getUserByEmail("mail@mail.mail")).thenReturn(userResponse);

        OrderResponse response = orderService.createOrder(orderRequest);
        assertEquals("mail@mail.mail", response.getUser().getEmail());
        verify(orderRepository).save(order);
    }

    @Test
    void testUpdateOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doAnswer(invocation -> {
            OrderRequest req = invocation.getArgument(0);
            order.setUserId(req.getUserId());
            return null;
        }).when(orderMapper).updateEntityFromRequest(orderRequest, order);

        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);
        when(userClient.getUserByEmail("mail@mail.mail")).thenReturn(userResponse);

        OrderResponse response = orderService.updateOrder(1L, orderRequest);
        assertEquals("mail@mail.mail", response.getUser().getEmail());
        verify(orderRepository).save(order);
    }

    @Test
    void testDeleteOrder() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(1L);

        assertDoesNotThrow(() -> orderService.deleteOrder(1L));
        verify(orderRepository).deleteById(1L);
    }
}