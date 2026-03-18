package com.recommend.server.service;

import com.recommend.server.dto.*;
import com.recommend.server.model.User;
import com.recommend.server.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

@Service
public class AuthService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email()))
            throw new RuntimeException("Email já existe");

        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow();

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse refresh(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new RuntimeException("Token ausente");

        String token = authHeader.substring(7);
        String email = jwtService.extractUserEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        if (!jwtService.isTokenValid(token, user))
            throw new RuntimeException("Token inválido");

        String newToken = jwtService.generateToken(user);

        return new AuthResponse(newToken);
    }

    public UserDTO getUser() {
        String email = getUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return user.getUserDTO();
    }

    public String getUserEmail() {
        return (String) Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getPrincipal();
    }

    public UserDTO addCoordinates(Coordinates coordinates) {
        String email = getUserEmail();
        userRepository.updateCoordinates(email, coordinates);
        return getUser();
    }
}
