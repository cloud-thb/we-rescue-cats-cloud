package io.werescuecats.backend.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private static final String VALID_SECRET = "12345678901234567890123456789012";

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", VALID_SECRET);
        ReflectionTestUtils.setField(jwtUtils, "expiration", 3_600_000L);
    }

    @Test
    void validateSecret_shouldThrowWhenShort() {
        JwtUtils utils = new JwtUtils();
        ReflectionTestUtils.setField(utils, "secret", "too-short");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> ReflectionTestUtils.invokeMethod(utils, "validateSecret"));

        assertEquals("JWT secret must be at least 256 bits (32 characters)", ex.getMessage());
    }

    @Test
    void generateToken_shouldIncludeSubjectAndClaims() {
        UserDetails user = new User("alice", "pw", Collections.emptyList());
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");

        String token = jwtUtils.generateToken(user, extraClaims);

        assertEquals("alice", jwtUtils.extractUsername(token));
        assertEquals("ADMIN", jwtUtils.extractClaim(token, claims -> claims.get("role", String.class)));
        assertFalse(jwtUtils.extractExpiration(token).before(new Date()));
        assertTrue(jwtUtils.validateToken(token, user));
    }

    @Test
    void validateToken_shouldReturnFalseForDifferentUser() {
        UserDetails user = new User("alice", "pw", Collections.emptyList());
        String token = jwtUtils.generateToken(user);

        UserDetails otherUser = new User("bob", "pw", Collections.emptyList());

        assertFalse(jwtUtils.validateToken(token, otherUser));
    }

    @Test
    void validateToken_shouldThrowWhenExpired() {
        JwtUtils expiredUtils = new JwtUtils();
        ReflectionTestUtils.setField(expiredUtils, "secret", VALID_SECRET);
        ReflectionTestUtils.setField(expiredUtils, "expiration", -1_000L);

        UserDetails user = new User("alice", "pw", Collections.emptyList());
        String token = expiredUtils.generateToken(user);

        assertThrows(ExpiredJwtException.class, () -> expiredUtils.validateToken(token, user));
    }
}
