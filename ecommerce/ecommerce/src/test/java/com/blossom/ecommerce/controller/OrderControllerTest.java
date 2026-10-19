package com.blossom.ecommerce.controller;

import com.blossom.ecommerce.dto.request.OrderRequest;
import com.blossom.ecommerce.dto.response.OrderResponse;
import com.blossom.ecommerce.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {
    @Mock OrderService orderService;
    @InjectMocks OrderController orderController;

    @Test
    void createsOrderForAuthenticatedUser() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("person@example.com", "password");

        var result = orderController.createOrder(new OrderRequest(), authentication);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(orderService).createOrder(eq("person@example.com"), any(OrderRequest.class));
    }

    @Test
    void returnsOrderHistoryForAuthenticatedUser() {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("person@example.com", "password");
        List<OrderResponse> history = List.of(new OrderResponse());
        when(orderService.getOrderHistory("person@example.com")).thenReturn(history);

        var result = orderController.getOrderHistory(authentication);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(history, result.getBody());
        verify(orderService).getOrderHistory("person@example.com");
    }
}
