package com.recommend.server.auth;

import com.recommend.server.dto.UserDTO;
import com.recommend.server.model.User;
import com.recommend.server.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtServiceTest {
    @Autowired
    JwtService jwtService;

    User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1);
        user.setEmail("teste@email.com");
    }

    @Test
    void shouldGenerateToken() {
        String token = jwtService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldExtractEmailFromToken() {
        String token = jwtService.generateToken(user);
        String email = jwtService.extractUserEmail(token);
        assertEquals("teste@email.com", email);
    }

    @Test
    void shouldValidateToken() {
        String token = jwtService.generateToken(user);
        boolean valid = jwtService.isTokenValid(token, user);
        assertTrue(valid);
    }
}
