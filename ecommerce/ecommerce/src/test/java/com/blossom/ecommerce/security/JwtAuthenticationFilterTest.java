package com.blossom.ecommerce.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @Mock JwtTokenProvider tokenProvider;
    @Mock CustomUserDetailsService userDetailsService;
    @InjectMocks JwtAuthenticationFilter filter;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticatesValidBearerTokenAndContinuesChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        UserDetails user = org.springframework.security.core.userdetails.User
                .withUsername("person@example.com").password("encoded").authorities("USER").build();
        when(tokenProvider.validateToken("valid-token")).thenReturn(true);
        when(tokenProvider.getEmailFromToken("valid-token")).thenReturn("person@example.com");
        when(userDetailsService.loadUserByUsername("person@example.com")).thenReturn(user);

        filter.doFilterInternal(request, response, chain);

        assertEquals("person@example.com",
                SecurityContextHolder.getContext().getAuthentication().getName());
        verify(chain).doFilter(request, response);
    }

    @Test
    void ignoresMissingOrInvalidBearerTokenAndContinuesChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, chain);
        request.addHeader("Authorization", "Basic abc");
        filter.doFilterInternal(request, response, chain);
        request.removeHeader("Authorization");
        request.addHeader("Authorization", "Bearer invalid");
        when(tokenProvider.validateToken("invalid")).thenReturn(false);
        filter.doFilterInternal(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain, times(3)).doFilter(request, response);
        verify(tokenProvider, never()).getEmailFromToken(anyString());
    }

    @Test
    void catchesAuthenticationSetupFailureAndContinuesChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        when(tokenProvider.validateToken("valid-token")).thenReturn(true);
        when(tokenProvider.getEmailFromToken("valid-token")).thenReturn("person@example.com");
        when(userDetailsService.loadUserByUsername("person@example.com"))
                .thenThrow(new IllegalStateException("lookup failed"));

        assertDoesNotThrow(() -> filter.doFilterInternal(request, response, chain));

        verify(chain).doFilter(request, response);
    }
}
