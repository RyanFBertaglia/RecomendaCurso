package com.recommend.server.auth;

import com.recommend.server.dto.AuthResponse;
import com.recommend.server.dto.Coordinates;
import com.recommend.server.dto.LoginRequest;
import com.recommend.server.dto.RegisterRequest;
import com.recommend.server.dto.UserDTO;
import com.recommend.server.model.Capelinho;
import com.recommend.server.model.User;
import com.recommend.server.repository.CapelinhoRepository;
import com.recommend.server.repository.UserRepository;
import com.recommend.server.service.AuthService;
import com.recommend.server.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.withSettings;

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

    @Mock
    CapelinhoRepository capelinhoRepository;

    @InjectMocks
    AuthService authService;

    @BeforeEach
    void setUpSecurityContext() {
        Authentication auth = mock(Authentication.class, withSettings().lenient());
        when(auth.getPrincipal()).thenReturn("test@email.com");
        SecurityContext securityContext = mock(SecurityContext.class, withSettings().lenient());
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

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

    @Test
    void shouldUpdateCapelinho() {
        User user = new User();
        user.setId(1);
        user.setName("Test");
        user.setEmail("test@email.com");
        user.setPassword("hashed");

        Capelinho capelinho = new Capelinho();
        capelinho.setId(2);
        capelinho.setName("Curioso");
        capelinho.setUrl("curioso.png");

        when(userRepository.findByEmail("test@email.com"))
                .thenReturn(Optional.of(user));
        when(capelinhoRepository.findById(2))
                .thenReturn(Optional.of(capelinho));
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO result = authService.updateCapelinho(2);

        assertEquals(2, result.capelinho());
        verify(capelinhoRepository).findById(2);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenCapelinhoNotFound() {
        User user = new User();
        user.setEmail("test@email.com");

        when(userRepository.findByEmail("test@email.com"))
                .thenReturn(Optional.of(user));
        when(capelinhoRepository.findById(99))
                .thenReturn(Optional.empty());

        try {
            authService.updateCapelinho(99);
        } catch (RuntimeException e) {
            assertEquals("Capelinho not found with ID: 99", e.getMessage());
        }
    }
}
