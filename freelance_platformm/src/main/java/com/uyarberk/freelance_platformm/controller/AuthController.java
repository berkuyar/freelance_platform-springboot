package com.uyarberk.freelance_platformm.controller;

import com.uyarberk.freelance_platformm.dto.AuthenticationResponse;
import com.uyarberk.freelance_platformm.dto.LoginRequest;
import com.uyarberk.freelance_platformm.dto.RefreshRequest;
import com.uyarberk.freelance_platformm.dto.RegisterRequest;
import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        AuthenticationResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        AuthenticationResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(@RequestBody RefreshRequest request) {
        AuthenticationResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "name", user.getName(),
                "surname", user.getSurname(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "createdAt", user.getCreatedAt()
        ));
    }
}