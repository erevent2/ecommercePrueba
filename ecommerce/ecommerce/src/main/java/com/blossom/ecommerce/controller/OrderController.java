package com.blossom.ecommerce.controller;

import com.blossom.ecommerce.dto.request.OrderRequest;
import com.blossom.ecommerce.dto.response.OrderResponse;
import com.blossom.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> createOrder(
            @Valid @RequestBody OrderRequest request, 
            @Parameter(hidden = true) Authentication authentication) { 
            
        String userEmail = authentication.getName(); 
        orderService.createOrder(userEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrderHistory(
            @Parameter(hidden = true) Authentication authentication) { 
            
        String userEmail = authentication.getName();
        List<OrderResponse> history = orderService.getOrderHistory(userEmail);
        return ResponseEntity.ok(history);
    }
}