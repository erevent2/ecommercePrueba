package com.blossom.ecommerce.service;

import com.blossom.ecommerce.dto.response.OrderResponse;
import com.blossom.ecommerce.model.Order;
import com.blossom.ecommerce.model.User;
import com.blossom.ecommerce.repository.OrderRepository;
import com.blossom.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class OrderHistoryIsolationTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private User firstUser;
    private User secondUser;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        userRepository.deleteAll();
        firstUser = saveUser("first@example.com");
        secondUser = saveUser("second@example.com");
        saveOrder(firstUser);
        saveOrder(firstUser);
        saveOrder(secondUser);
    }

    @Test
    void returnsOnlyOrdersOwnedByTheAuthenticatedUser() {
        List<OrderResponse> firstUserOrders = orderService.getOrderHistory(firstUser.getEmail());
        List<OrderResponse> secondUserOrders = orderService.getOrderHistory(secondUser.getEmail());

        assertEquals(2, firstUserOrders.size());
        assertEquals(1, secondUserOrders.size());
        assertTrue(firstUserOrders.stream().noneMatch(order ->
                secondUserOrders.stream().anyMatch(other -> other.getId().equals(order.getId()))));
    }

    private User saveUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("encoded-password");
        return userRepository.save(user);
    }

    private void saveOrder(User user) {
        Order order = new Order();
        order.setUser(user);
        orderRepository.save(order);
    }
}
