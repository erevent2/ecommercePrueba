package com.blossom.ecommerce.controller;

import com.blossom.ecommerce.dto.request.AuthRequest;
import com.blossom.ecommerce.dto.response.AuthResponse;
import com.blossom.ecommerce.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerUnitTest {
    @Mock AuthService authService;
    @InjectMocks AuthController authController;

    @Test
    void returnsTokenForLogin() {
        AuthRequest request = new AuthRequest();
        AuthResponse response = new AuthResponse("token");
        when(authService.login(request)).thenReturn(response);

        var result = authController.login(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(response, result.getBody());
    }

    @Test
    void registersAndReturnsCreated() {
        AuthRequest request = new AuthRequest();

        var result = authController.register(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNull(result.getBody());
        verify(authService).register(request);
    }
}
