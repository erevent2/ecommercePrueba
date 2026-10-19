package com.blossom.ecommerce.service;

import com.blossom.ecommerce.dto.request.OrderRequest;
import com.blossom.ecommerce.dto.response.OrderResponse;
import com.blossom.ecommerce.model.Order;
import com.blossom.ecommerce.model.OrderItem;
import com.blossom.ecommerce.model.Product;
import com.blossom.ecommerce.model.User;
import com.blossom.ecommerce.repository.OrderRepository;
import com.blossom.ecommerce.repository.ProductRepository;
import com.blossom.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import com.blossom.ecommerce.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createOrder(String email, OrderRequest request) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        // order.setStatus(OrderStatus.PENDING); // Si definiste el Enum

        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + itemRequest.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice()); // Guardamos el precio histórico

            order.getItems().add(orderItem);
        }

        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderHistory(String email) {
        List<Order> userOrders = orderRepository.findByUserEmailOrderByOrderDateDesc(email);

        return userOrders.stream()
                .map(this::mapOrderToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapOrderToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderDate(order.getOrderDate());
        response.setStatus(order.getStatus() != null ? order.getStatus().name() : "COMPLETED");

        // Mapeamos los ítems de la orden
        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> {
                    OrderResponse.OrderItemResponse itemResp = new OrderResponse.OrderItemResponse();
                    itemResp.setProductId(item.getProduct().getId());
                    itemResp.setProductName(item.getProduct().getName());
                    itemResp.setQuantity(item.getQuantity());
                    itemResp.setUnitPrice(item.getUnitPrice());
                    return itemResp;
                })
                .collect(Collectors.toList());

        response.setItems(itemResponses);
        return response;
    }
}