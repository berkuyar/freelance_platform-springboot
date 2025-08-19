package com.uyarberk.freelance_platformm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false, length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Long relatedPostId;
    private Long relatedBidId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    public enum NotificationType {
        NEW_BID,           // Yeni bid geldi
        BID_ACCEPTED,      // Bid kabul edildi
        BID_REJECTED,      // Bid reddedildi
        POST_UPDATE,       // Post güncellendi
        POST_COMPLETED,    // Post tamamlandı
        DEADLINE_REMINDER, // Deadline yaklaşıyor
        SYSTEM             // Sistem bildirimi
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
}
