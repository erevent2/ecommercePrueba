package com.blossom.ecommerce.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void returnsNotFoundDetails() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/products/10");

        var response = handler.handleResourceNotFoundException(
                new ResourceNotFoundException("missing"), request);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Not Found", response.getBody().get("error"));
        assertEquals("missing", response.getBody().get("message"));
        assertEquals("/api/products/10", response.getBody().get("path"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void returnsFieldValidationErrors() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "email", "invalid email"));
        MethodArgumentNotValidException exception =
                mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/auth/register");

        var response = handler.handleValidationExceptions(exception, request);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Validation Error", response.getBody().get("error"));
        assertEquals("invalid email", ((Map<?, ?>) response.getBody().get("errors")).get("email"));
    }

    @Test
    void hidesUnexpectedExceptionDetails() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/orders");

        var response = handler.handleGlobalException(new IllegalStateException("private detail"), request);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Ocurrió un error inesperado en el servidor", response.getBody().get("message"));
        assertNotEquals("private detail", response.getBody().get("message"));
    }
}
