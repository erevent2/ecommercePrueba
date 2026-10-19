package com.blossom.ecommerce.security;

import com.blossom.ecommerce.model.User;
import com.blossom.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock UserRepository userRepository;
    @InjectMocks CustomUserDetailsService userDetailsService;

    @Test
    void loadsUserDetailsFromRepository() {
        User user = new User();
        user.setEmail("person@example.com");
        user.setPassword("encoded");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername(user.getEmail());

        assertEquals(user.getEmail(), details.getUsername());
        assertEquals("encoded", details.getPassword());
        assertTrue(details.getAuthorities().isEmpty());
    }

    @Test
    void throwsWhenEmailIsUnknown() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missing@example.com"));
    }
}
