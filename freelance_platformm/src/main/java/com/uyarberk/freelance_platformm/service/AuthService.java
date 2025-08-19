package com.uyarberk.freelance_platformm.service;

import com.uyarberk.freelance_platformm.config.JwtUtil;
import com.uyarberk.freelance_platformm.dto.AuthenticationResponse;
import com.uyarberk.freelance_platformm.dto.LoginRequest;
import com.uyarberk.freelance_platformm.dto.RefreshRequest;
import com.uyarberk.freelance_platformm.dto.RegisterRequest;
import com.uyarberk.freelance_platformm.exception.InvalidCredentialsException;
import com.uyarberk.freelance_platformm.exception.InvalidTokenException;
import com.uyarberk.freelance_platformm.exception.UserAlreadyExistsException;
import com.uyarberk.freelance_platformm.exception.UserNotFoundException;
import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthenticationResponse register(RegisterRequest request) {
        // Username/email kontrolü
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Bu kullanıcı adı zaten kullanılıyor");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Bu e-posta adresi zaten kullanılıyor");
        }

        // User oluştur
        User user = new User();
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // Token üret
        String accessToken = jwtUtil.generateAccessToken(savedUser);
        String refreshToken = jwtUtil.generateRefreshToken(savedUser);

        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse login(LoginRequest request) {
        try {
            // Authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = (User) authentication.getPrincipal();

            // Token üret
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            return new AuthenticationResponse(accessToken, refreshToken);

        } catch (Exception e) {
            throw new InvalidCredentialsException("Kullanıcı adı veya şifre hatalı");
        }
    }

    public AuthenticationResponse refresh(RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        // Refresh token geçerli mi?
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Geçersiz veya süresi dolmuş token");
        }

        // User bilgisini getir
        Long userId = jwtUtil.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı"));

        // Yeni tokenlar üret
        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        return new AuthenticationResponse(newAccessToken, newRefreshToken);
    }
}