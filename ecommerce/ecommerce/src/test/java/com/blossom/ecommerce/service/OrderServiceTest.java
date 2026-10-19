package com.blossom.ecommerce.service;

import com.blossom.ecommerce.dto.request.OrderRequest;
import com.blossom.ecommerce.dto.response.OrderResponse;
import com.blossom.ecommerce.exception.ResourceNotFoundException;
import com.blossom.ecommerce.model.*;
import com.blossom.ecommerce.repository.OrderRepository;
import com.blossom.ecommerce.repository.ProductRepository;
import com.blossom.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock OrderRepository orderRepository;
    @Mock ProductRepository productRepository;
    @Mock UserRepository userRepository;
    @InjectMocks OrderService orderService;

    @Test
    void createOrderPersistsItemsWithHistoricalProductPrice() {
        User user = new User();
        Product product = product();
        when(userRepository.findByEmail("person@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(3L)).thenReturn(Optional.of(product));
        OrderRequest request = new OrderRequest();
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(3L);
        item.setQuantity(2);
        request.setItems(List.of(item));

        orderService.createOrder("person@example.com", request);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order saved = captor.getValue();
        assertSame(user, saved.getUser());
        assertNotNull(saved.getOrderDate());
        assertEquals(1, saved.getItems().size());
        assertEquals(2, saved.getItems().get(0).getQuantity());
        assertEquals(product.getPrice(), saved.getItems().get(0).getUnitPrice());
        assertSame(saved, saved.getItems().get(0).getOrder());
    }

    @Test
    void createOrderFailsWhenUserDoesNotExist() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder("missing@example.com", new OrderRequest()));
        verifyNoInteractions(productRepository, orderRepository);
    }

    @Test
    void createOrderFailsWhenProductDoesNotExist() {
        when(userRepository.findByEmail("person@example.com")).thenReturn(Optional.of(new User()));
        when(productRepository.findById(404L)).thenReturn(Optional.empty());
        OrderRequest request = request(404L, 1);

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.createOrder("person@example.com", request));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrderHistoryMapsItemsAndDefaultsMissingStatus() {
        Order order = new Order();
        order.setId(12L);
        order.setOrderDate(LocalDateTime.of(2025, 1, 2, 3, 4));
        order.setStatus(null);
        OrderItem item = new OrderItem();
        item.setProduct(product());
        item.setQuantity(3);
        item.setUnitPrice(new BigDecimal("19.90"));
        order.getItems().add(item);
        when(orderRepository.findByUserEmailOrderByOrderDateDesc("person@example.com"))
                .thenReturn(List.of(order));

        List<OrderResponse> result = orderService.getOrderHistory("person@example.com");

        assertEquals(1, result.size());
        assertEquals("COMPLETED", result.get(0).getStatus());
        assertEquals(12L, result.get(0).getId());
        assertEquals(3, result.get(0).getItems().get(0).getQuantity());
        assertEquals("Product", result.get(0).getItems().get(0).getProductName());
    }

    @Test
    void getOrderHistoryMapsExplicitStatusAndEmptyItems() {
        Order order = new Order();
        order.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findByUserEmailOrderByOrderDateDesc("person@example.com"))
                .thenReturn(List.of(order));

        List<OrderResponse> result = orderService.getOrderHistory("person@example.com");

        assertEquals("CANCELLED", result.get(0).getStatus());
        assertTrue(result.get(0).getItems().isEmpty());
    }

    @Test
    void getOrderHistoryUsesAuthenticatedEmailAsScope() {
        when(orderRepository.findByUserEmailOrderByOrderDateDesc("missing@example.com"))
                .thenReturn(List.of());

        assertTrue(orderService.getOrderHistory("missing@example.com").isEmpty());
        verify(orderRepository).findByUserEmailOrderByOrderDateDesc("missing@example.com");
    }

    private Product product() {
        PhysicalProduct product = new PhysicalProduct();
        product.setId(3L);
        product.setName("Product");
        product.setPrice(new BigDecimal("19.90"));
        return product;
    }

    private OrderRequest request(Long productId, int quantity) {
        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest();
        item.setProductId(productId);
        item.setQuantity(quantity);
        OrderRequest request = new OrderRequest();
        request.setItems(List.of(item));
        return request;
    }
}
