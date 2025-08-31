package com.uyarberk.freelance_platformm.service;

import com.uyarberk.freelance_platformm.dto.ChangePasswordDto;
import com.uyarberk.freelance_platformm.dto.UpdateProfileRequest;
import com.uyarberk.freelance_platformm.dto.UserProfileDto;
import com.uyarberk.freelance_platformm.dto.UserStatsDto;
import com.uyarberk.freelance_platformm.exception.UserNotFoundException;
import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UpdateProfileRequest updateRequest;
    private ChangePasswordDto changePasswordDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setName("Test");
        testUser.setSurname("User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(User.Role.FREELANCER);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setCity("Istanbul");
        testUser.setPhone("5551234567");
        testUser.setSkills("Java, Spring");
        testUser.setBio("Test bio");

        updateRequest = new UpdateProfileRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setSurname("Updated Surname");
        updateRequest.setCity("Ankara");
        updateRequest.setPhone("5559876543");
        updateRequest.setSkills("Python, Django");
        updateRequest.setBio("Updated bio");

        changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setOldPassword("oldPassword123");
        changePasswordDto.setNewPassword("newPassword123");
        changePasswordDto.setConfirmNewPassword("newPassword123");
    }

    @Test
    void getUserProfile_Basarili() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        UserProfileDto result = userService.getUserProfile(1L);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getCity(), result.getCity());
        assertEquals(testUser.getSkills(), result.getSkills());

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserProfile_UserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserProfile(1L);
        });

        verify(userRepository).findById(1L);
    }

    @Test
    void updateUserProfile_Basarili() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserProfileDto result = userService.updateUserProfile(1L, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(updateRequest.getName(), result.getName());
        assertEquals(updateRequest.getSurname(), result.getSurname());
        assertEquals(updateRequest.getCity(), result.getCity());
        assertEquals(updateRequest.getPhone(), result.getPhone());
        assertEquals(updateRequest.getSkills(), result.getSkills());
        assertEquals(updateRequest.getBio(), result.getBio());

        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    void getUserStats_Basarili() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.countPostsByUserId(1L)).thenReturn(10L);
        when(userRepository.countBidsByUserId(1L)).thenReturn(20L);
        when(userRepository.countAcceptedBidsByUserId(1L)).thenReturn(8L);
        when(userRepository.countRejectedBidsByUserId(1L)).thenReturn(2L);

        // When
        UserStatsDto result = userService.getUserStats(1L);

        // Then
        assertNotNull(result);
        assertEquals(10L, result.getTotalPosts());
        assertEquals(20L, result.getTotalBids());
        assertEquals(8L, result.getAcceptedBids());
        assertEquals(2L, result.getRejectedBids());
        assertEquals(10L, result.getPendingBids()); // 20 - 8 - 2
        assertEquals(40.0, result.getSuccessRate()); // 8 * 100.0 / 20

        verify(userRepository).findById(1L);
        verify(userRepository).countPostsByUserId(1L);
        verify(userRepository).countBidsByUserId(1L);
        verify(userRepository).countAcceptedBidsByUserId(1L);
        verify(userRepository).countRejectedBidsByUserId(1L);
    }

    @Test
    void changePassword_Basarili() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword123", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.changePassword(1L, changePasswordDto);

        // Then
        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("oldPassword123", "encodedPassword");
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(testUser);
    }

    @Test
    void changePassword_EskiSifreYanlis() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword123", testUser.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.changePassword(1L, changePasswordDto);
        });

        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("oldPassword123", testUser.getPassword());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_YeniSifrelerEslesmez() {
        // Given
        changePasswordDto.setConfirmNewPassword("farkliSifre");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword123", testUser.getPassword())).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            userService.changePassword(1L, changePasswordDto);
        });

        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("oldPassword123", testUser.getPassword());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }
}