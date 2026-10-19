package com.blossom.ecommerce.config;

import com.blossom.ecommerce.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SecurityConfigCorsTest {
    @Test
    void corsAllowsConfiguredLocalOriginsAndRejectsUnknownOrigins() {
        SecurityConfig securityConfig = new SecurityConfig(mock(JwtAuthenticationFilter.class));
        ReflectionTestUtils.setField(securityConfig, "allowedOrigins",
                "http://localhost:5173, http://localhost");
        var corsSource = securityConfig.corsConfigurationSource();

        CorsConfiguration localOrigin = corsSource.getCorsConfiguration(request("http://localhost"));
        CorsConfiguration viteOrigin = corsSource.getCorsConfiguration(request("http://localhost:5173"));
        CorsConfiguration externalOrigin = corsSource.getCorsConfiguration(request("https://untrusted.example"));

        assertNotNull(localOrigin);
        assertTrue(localOrigin.checkOrigin("http://localhost") != null);
        assertTrue(viteOrigin.checkOrigin("http://localhost:5173") != null);
        assertNull(externalOrigin.checkOrigin("https://untrusted.example"));
        assertTrue(localOrigin.getAllowCredentials());
        assertTrue(localOrigin.getAllowedMethods().contains("OPTIONS"));
        assertTrue(localOrigin.getAllowedHeaders().contains("Authorization"));
    }

    private MockHttpServletRequest request(String origin) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/products");
        request.addHeader("Origin", origin);
        return request;
    }
}
