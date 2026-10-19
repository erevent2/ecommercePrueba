package com.blossom.ecommerce.service;

import com.blossom.ecommerce.dto.request.AuthRequest;
import com.blossom.ecommerce.dto.response.AuthResponse;
import com.blossom.ecommerce.exception.ResourceNotFoundException;
import com.blossom.ecommerce.model.User;
import com.blossom.ecommerce.repository.UserRepository;
import com.blossom.ecommerce.security.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock AuthenticationManager authenticationManager;
    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtTokenProvider tokenProvider;
    @InjectMocks AuthService authService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerEncodesPasswordAndPersistsUser() {
        AuthRequest request = request();
        when(userRepository.findByEmail("person@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret")).thenReturn("encoded");

        authService.register(request);

        verify(userRepository).save(argThat(user -> user.getEmail().equals("person@example.com")
                && user.getPassword().equals("encoded")));
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.findByEmail("person@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () -> authService.register(request()));
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginAuthenticatesAndReturnsToken() {
        AuthRequest request = request();
        Authentication authentication = mock(Authentication.class);
        when(userRepository.findByEmail("person@example.com")).thenReturn(Optional.of(new User()));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        AuthResponse result = authService.login(request);

        assertEquals("jwt-token", result.getToken());
        assertSame(authentication, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void loginRejectsUnknownUserBeforeAuthenticating() {
        when(userRepository.findByEmail("person@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.login(request()));
        verifyNoInteractions(authenticationManager);
    }

    private AuthRequest request() {
        AuthRequest request = new AuthRequest();
        request.setEmail("person@example.com");
        request.setPassword("secret");
        return request;
    }
}
