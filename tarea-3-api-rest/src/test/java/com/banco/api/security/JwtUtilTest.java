package com.banco.api.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    @Test
    void generateAndValidateToken() {
        JwtUtil util = new JwtUtil("secret123", 60);
        String token = util.generateToken("admin");

        assertEquals("admin", util.validateAndExtractSubject(token));
    }

    @Test
    void expiredTokenShouldFail() {
        JwtUtil util = new JwtUtil("secret123", -1);
        String token = util.generateToken("admin");

        assertThrows(IllegalArgumentException.class, () -> util.validateAndExtractSubject(token));
    }
}