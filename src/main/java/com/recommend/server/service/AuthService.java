package com.recommend.server.service;

import com.recommend.server.dto.*;
import com.recommend.server.exception.BadCredentials;
import com.recommend.server.exception.EmailAlreadyExists;
import com.recommend.server.exception.LocationNotProvided;
import com.recommend.server.exception.TokenInvalid;
import com.recommend.server.model.Course;
import com.recommend.server.model.History;
import com.recommend.server.model.User;
import com.recommend.server.repository.HistoryRepository;
import com.recommend.server.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
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
    HistoryRepository historyRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email()))
            throw new EmailAlreadyExists();

        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setLocale(request.locale());
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(BadCredentials::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentials();
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
                .orElseThrow(BadCredentials::new);

        if (!jwtService.isTokenValid(token, user))
            throw new TokenInvalid("Invalid token");

        String newToken = jwtService.generateToken(user);

        return new AuthResponse(newToken);
    }

    public UserDTO getUserDTO() {
        String email = getUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return user.getUserDTO();
    }

    public User getUser() {
        String email = getUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(BadCredentials::new);
    }

    public String getUserEmail() {
        if (SecurityContextHolder.getContext().getAuthentication() == null)
            throw new BadCredentials("User not authenticated");

        return (String) Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getPrincipal();
    }

    public UserDTO addCoordinates(Coordinates coordinates) {
        String email = getUserEmail();
        if (email == null)
            throw new BadCredentials("User not authenticated");
        if (coordinates == null)
            throw new LocationNotProvided("Coordinates not provided");

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        user.setLocale(coordinates);

        userRepository.save(user);
        return user.getUserDTO();
    }

    public History addHistory(Course course) {
        User user = getUser();
        if (course == null)
            throw new BadCredentials("Course not provided");

        History history = new History();
        history.setUser(user);
        history.setCourse(course);
        history.setAccessedAt(new Date());

        return historyRepository.save(history);
    }

    public List<History> getHistory() {
        return historyRepository.findByUserIdOrderByAccessedAtDesc(getUser().getId());
    }
}
