package com.uyarberk.freelance_platformm.dto;

import com.uyarberk.freelance_platformm.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    
    private Long id;
    private String title;
    private String message;
    private Notification.NotificationType type;
    private boolean isRead;
    private Long relatedPostId;
    private Long relatedBidId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    
    public static NotificationResponse fromEntity(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType());
        response.setRead(notification.isRead());
        response.setRelatedPostId(notification.getRelatedPostId());
        response.setRelatedBidId(notification.getRelatedBidId());
        response.setCreatedAt(notification.getCreatedAt());
        response.setReadAt(notification.getReadAt());
        return response;
    }
}