package com.project.stockanalysis.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Base64 encoded secret key for testing (matches application.properties)
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "dGhpcy1pcy1hLXZlcnktc2VjcmV0LWtleS1mb3Itc3RvY2stYW5hbHlzaXMtYXBwLTIwMjQ=");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", 86400000L); // 24 hours

        userDetails = new User("testuser", "password123", new ArrayList<>());
    }

    @Test
    void testGenerateTokenAndExtractUsername() {
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);
        
        String username = jwtUtil.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void testIsTokenValid() {
        String token = jwtUtil.generateToken(userDetails);
        assertTrue(jwtUtil.isTokenValid(token, userDetails));
        
        UserDetails otherUser = new User("otheruser", "password", new ArrayList<>());
        assertFalse(jwtUtil.isTokenValid(token, otherUser));
    }

    @Test
    void testExtractExpiration() {
        String token = jwtUtil.generateToken(userDetails);
        Date expiration = jwtUtil.extractExpiration(token);
        
        // Token expiration should be in the future
        assertTrue(expiration.after(new Date()));
    }
}
