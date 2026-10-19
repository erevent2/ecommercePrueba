package com.blossom.ecommerce.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {
    private static final String SECRET = "AnExampleSecretThatIsLongEnoughForHmacShaSigning123456";

    @Test
    void generatesValidTokenAndExtractsSubject() {
        JwtTokenProvider provider = provider(86_400_000);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("person@example.com", "password");

        String token = provider.generateToken(authentication);

        assertTrue(provider.validateToken(token));
        assertEquals("person@example.com", provider.getEmailFromToken(token));
    }

    @Test
    void rejectsMalformedExpiredAndDifferentlySignedTokens() {
        JwtTokenProvider provider = provider(86_400_000);
        JwtTokenProvider expiredProvider = provider(-1);
        String expired = expiredProvider.generateToken(
                new UsernamePasswordAuthenticationToken("person@example.com", "password"));
        JwtTokenProvider otherKeyProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(otherKeyProvider, "jwtSecret",
                "AnotherExampleSecretThatIsLongEnoughForHmacShaSigning123456");

        assertFalse(provider.validateToken("not-a-jwt"));
        assertFalse(provider.validateToken(expired));
        assertFalse(provider.validateToken(otherKeyProvider.generateToken(
                new UsernamePasswordAuthenticationToken("person@example.com", "password"))));
    }

    private JwtTokenProvider provider(int expiration) {
        JwtTokenProvider provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(provider, "jwtExpirationInMs", expiration);
        return provider;
    }
}
