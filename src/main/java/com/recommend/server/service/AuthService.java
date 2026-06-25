package com.recommend.server.service;

import com.recommend.server.dto.*;
import com.recommend.server.exception.BadCredentials;
import com.recommend.server.exception.EmailAlreadyExists;
import com.recommend.server.exception.LocationNotProvided;
import com.recommend.server.exception.TokenInvalid;
import com.recommend.server.model.Capelinho;
import com.recommend.server.model.Course;
import com.recommend.server.model.History;
import com.recommend.server.model.User;
import com.recommend.server.repository.CapelinhoRepository;
import com.recommend.server.repository.HistoryRepository;
import com.recommend.server.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
    CapelinhoRepository capelinhoRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Transactional
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

    @Transactional
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
                .orElseThrow(BadCredentials::new);

        return user.getUserDTO();
    }

    public User getUser() {
        String email = getUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(BadCredentials::new);
    }

    public String getUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            throw new BadCredentials("User not authenticated");

        Object principal = authentication.getPrincipal();
        if (principal instanceof String email)
            return email;
        throw new BadCredentials("User not authenticated");
    }

    @Transactional
    public UserDTO addCoordinates(Coordinates coordinates) {
        String email = getUserEmail();
        if (email == null)
            throw new BadCredentials("User not authenticated");
        if (coordinates == null)
            throw new LocationNotProvided("Coordinates not provided");

        User user = userRepository.findByEmail(email)
                .orElseThrow(BadCredentials::new);

        user.setLocale(coordinates);

        userRepository.save(user);
        return user.getUserDTO();
    }

    @Transactional
    @Async
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

    public Page<History> getHistoryPage(Pageable pageable) {
        return historyRepository.findByUserId(getUser().getId(), pageable);
    }

    @Transactional
    public UserDTO updateCapelinho(Integer capelinhoId) {
        User user = getUser();
        Capelinho capelinho = capelinhoRepository.findById(capelinhoId)
                .orElseThrow(() -> new RuntimeException("Capelinho not found with ID: " + capelinhoId));

        user.setCapelinho(capelinho);
        userRepository.save(user);
        return user.getUserDTO();
    }
}