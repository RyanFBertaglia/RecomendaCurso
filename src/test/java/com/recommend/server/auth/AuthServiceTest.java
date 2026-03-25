package com.recommend.server.auth;

import com.recommend.server.dto.AuthResponse;
import com.recommend.server.dto.Coordinates;
import com.recommend.server.dto.LoginRequest;
import com.recommend.server.dto.RegisterRequest;
import com.recommend.server.model.User;
import com.recommend.server.repository.UserRepository;
import com.recommend.server.service.AuthService;
import com.recommend.server.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtService jwtService;

    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    AuthService authService;

    @Test
    void shouldRegisterUser() {

        RegisterRequest request =
                new RegisterRequest("Ryan","ryan@email.com","123456", new Coordinates(32.0, 32.0));

        when(userRepository.existsByEmail("ryan@email.com"))
                .thenReturn(false);
        when(passwordEncoder.encode("123456"))
                .thenReturn("hashed");

        authService.register(request);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldLoginAndReturnToken() {
        String email = "ryan@email.com";
        String rawPassword = "123456";
        String encodedPassword = "encoded-password";
        LoginRequest request = new LoginRequest(email, rawPassword);

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword))
                .thenReturn(true);
        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        AuthResponse response = authService.login(request);
        assertEquals("jwt-token", response.token());

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
        verify(jwtService).generateToken(user);
    }
    @Test
    void shouldRefreshToken() {

        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer old-token");

        User user = new User();
        user.setEmail("ryan@email.com");

        when(jwtService.extractUserEmail("old-token"))
                .thenReturn("ryan@email.com");
        when(userRepository.findByEmail("ryan@email.com"))
                .thenReturn(Optional.of(user));
        when(jwtService.isTokenValid("old-token", user))
                .thenReturn(true);
        when(jwtService.generateToken(user))
                .thenReturn("new-token");

        AuthResponse response = authService.refresh(request);
        assertEquals("new-token", response.token());
    }
}
