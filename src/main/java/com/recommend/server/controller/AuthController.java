package com.recommend.server.controller;

import com.recommend.server.dto.*;
import com.recommend.server.model.Course;
import com.recommend.server.model.History;
import com.recommend.server.service.AuthService;
import com.recommend.server.service.DataService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @Autowired
    DataService dataService;

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
        return ResponseEntity.ok(authService.getUserDTO());
    }

    @PostMapping("/coordinates")
    public ResponseEntity<UserDTO> addCoordinates(@RequestBody Coordinates coordinates) {
        return ResponseEntity.ok(authService.addCoordinates(coordinates));
    }

    @PostMapping("/history")
    public ResponseEntity<History> addHistory(@RequestParam Integer idCourse) {
        if (idCourse == null) return ResponseEntity.badRequest().build();
        Course course = dataService.findOneCourse(idCourse);
        return ResponseEntity.ok().body(authService.addHistory(course));
    }

    @GetMapping("/history")
    public ResponseEntity<List<History>> getHistory() {
        return ResponseEntity.ok(authService.getHistory());
    }
}
