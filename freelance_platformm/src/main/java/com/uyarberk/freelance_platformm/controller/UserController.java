package com.uyarberk.freelance_platformm.controller;


import com.uyarberk.freelance_platformm.dto.ChangePasswordDto;
import com.uyarberk.freelance_platformm.dto.UpdateProfileRequest;
import com.uyarberk.freelance_platformm.dto.UserProfileDto;
import com.uyarberk.freelance_platformm.dto.UserStatsDto;
import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/profile")

    public ResponseEntity<UserProfileDto>  getUserProfile(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        UserProfileDto userProfileDto = userService.getUserProfile(userId);
        return ResponseEntity.ok(userProfileDto);

    }

     @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateUserProfile(@AuthenticationPrincipal User user, @RequestBody UpdateProfileRequest a) {
        Long userId = user.getId();
        UserProfileDto userProfileDto = userService.updateUserProfile(userId, a);
        return ResponseEntity.ok(userProfileDto);
     }

     @GetMapping("/stats")
    public ResponseEntity<UserStatsDto> getUserStats(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        UserStatsDto userStatsDto = userService.getUserStats(userId);
        return ResponseEntity.ok(userStatsDto);
     }
     @DeleteMapping("/account")
    public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        userService.deleteAccount(userId);
        return ResponseEntity.ok().build();
     }

     @PutMapping("/change/password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal User user, @RequestBody ChangePasswordDto changePasswordDto) {
        Long userId = user.getId();
        userService.changePassword(userId, changePasswordDto);
        return ResponseEntity.ok("Şifre güncellendi");
     }


}
