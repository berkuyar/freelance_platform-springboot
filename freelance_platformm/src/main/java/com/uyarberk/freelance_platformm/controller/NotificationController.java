package com.uyarberk.freelance_platformm.controller;

import com.uyarberk.freelance_platformm.dto.NotificationResponse;
import com.uyarberk.freelance_platformm.model.Notification;
import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(@AuthenticationPrincipal User user) {
        List<NotificationResponse> notifications = notificationService.getUserNotifications(user.getId());
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(@AuthenticationPrincipal User user) {
        List<NotificationResponse> notifications = notificationService.getUnreadNotifications(user.getId());
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal User user) {
        Long count = notificationService.getUnreadCount(user.getId());
        return ResponseEntity.ok(count);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByType(
            @PathVariable Notification.NotificationType type,
            @AuthenticationPrincipal User user) {
        List<NotificationResponse> notifications = notificationService.getNotificationsByType(user.getId(), type);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/recent/{days}")
    public ResponseEntity<List<NotificationResponse>> getRecentNotifications(
            @PathVariable int days,
            @AuthenticationPrincipal User user) {
        List<NotificationResponse> notifications = notificationService.getRecentNotifications(user.getId(), days);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cleanup/{days}")
    public ResponseEntity<Void> deleteOldNotifications(
            @PathVariable int days,
            @AuthenticationPrincipal User user) {
        notificationService.deleteOldNotifications(user.getId(), days);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/system")
    public ResponseEntity<Void> createSystemNotification(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String message) {
        notificationService.createSystemNotification(userId, title, message);
        return ResponseEntity.ok().build();
    }
}