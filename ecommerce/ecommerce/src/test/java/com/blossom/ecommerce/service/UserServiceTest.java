package com.blossom.ecommerce.service;

import com.blossom.ecommerce.exception.ResourceNotFoundException;
import com.blossom.ecommerce.model.User;
import com.blossom.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock UserRepository userRepository;
    @InjectMocks UserService userService;

    @Test
    void findsUserById() {
        User user = new User();
        when(userRepository.findById(4L)).thenReturn(Optional.of(user));

        assertSame(user, userService.getUserById(4L));
    }

    @Test
    void throwsWhenUserIdIsMissing() {
        when(userRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(4L));
    }

    @Test
    void findsUserByEmail() {
        User user = new User();
        when(userRepository.findByEmail("person@example.com")).thenReturn(Optional.of(user));

        assertSame(user, userService.getUserByEmail("person@example.com"));
    }

    @Test
    void throwsWhenEmailIsMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserByEmail("missing@example.com"));
    }
}
