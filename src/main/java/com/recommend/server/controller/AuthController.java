package com.recommend.server.controller;

import com.recommend.server.dto.*;
import com.recommend.server.model.User;
import com.recommend.server.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return new ResponseEntity<>(authService.register(request),HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(HttpServletRequest request) {
        return authService.refresh(request);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me() {
        return ResponseEntity.ok(authService.getUser());
    }

    @PostMapping("/coordinates")
    public ResponseEntity<UserDTO> addCoordinates(@RequestBody Coordinates coordinates) {
        return ResponseEntity.ok(authService.addCoordinates(coordinates));
    }
}
