package com.uyarberk.freelance_platformm.service;

import com.uyarberk.freelance_platformm.config.JwtUtil;
import com.uyarberk.freelance_platformm.dto.AuthenticationResponse;
import com.uyarberk.freelance_platformm.dto.LoginRequest;
import com.uyarberk.freelance_platformm.dto.RegisterRequest;
import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setName("Test");
        registerRequest.setSurname("User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole("FREELANCER");
        
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
    }


    @Test
    void registerTesti_Basarili() {
        // Given - Test verilerini hazırla
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jwtUtil.generateAccessToken(any(User.class))).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn("refresh-token");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When - Test edilecek method'u çağır
        AuthenticationResponse response = authService.register(registerRequest);

        // Then - Sonuçları kontrol et
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        
        // Mock'ların çağrıldığını kontrol et
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateAccessToken(any(User.class));
        verify(jwtUtil).generateRefreshToken(any(User.class));
    }

    @Test
    void loginTesti_Basarili() {
        // Given - Test verilerini hazırla
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("encodedPassword123");
        
        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUser);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(jwtUtil.generateAccessToken(mockUser)).thenReturn("login-access-token");
        when(jwtUtil.generateRefreshToken(mockUser)).thenReturn("login-refresh-token");

        // When - Test edilecek method'u çağır
        AuthenticationResponse response = authService.login(loginRequest);

        // Then - Sonuçları kontrol et
        assertNotNull(response);
        assertEquals("login-access-token", response.getAccessToken());
        assertEquals("login-refresh-token", response.getRefreshToken());
        
        // Mock'ların çağrıldığını kontrol et
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateAccessToken(mockUser);
        verify(jwtUtil).generateRefreshToken(mockUser);
    }
}