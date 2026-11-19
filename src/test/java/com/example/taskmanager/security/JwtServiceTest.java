package com.example.taskmanager.security;

import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                "dGVzdC1vbmx5LWp3dC1zaWduaW5nLWtleS0zMi1ieXRlcyE="
        );

        ReflectionTestUtils.setField(
                jwtService,
                "expiration",
                86400000L
        );

        user = new User(
                1L,
                "user@test.com",
                "password",
                Role.ROLE_USER
        );
    }

    @Test
    void generateToken_shouldContainUsername() {
        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();

        assertThat(jwtService.extractUsername(token))
                .isEqualTo("user@test.com");
    }

    @Test
    void isTokenValid_withValidToken_shouldReturnTrue() {
        String token = jwtService.generateToken(user);

        boolean result = jwtService.isTokenValid(
                token,
                user
        );

        assertThat(result).isTrue();
    }

    @Test
    void extractUsername_shouldReturnCorrectEmail() {
        String token = jwtService.generateToken(user);

        String email = jwtService.extractUsername(token);

        assertThat(email)
                .isEqualTo("user@test.com");
    }
}
