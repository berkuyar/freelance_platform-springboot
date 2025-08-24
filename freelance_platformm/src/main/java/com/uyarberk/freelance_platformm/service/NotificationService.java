package com.uyarberk.freelance_platformm.service;

import com.uyarberk.freelance_platformm.dto.NotificationResponse;
import com.uyarberk.freelance_platformm.model.Notification;
import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.model.Post;
import com.uyarberk.freelance_platformm.model.Bid;
import com.uyarberk.freelance_platformm.repository.NotificationRepository;
import com.uyarberk.freelance_platformm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void createBidNotification(Post post, Bid bid) {
        User employer = post.getUser();
        
        Notification notification = new Notification();
        notification.setTitle("Yeni Teklif Geldi");
        notification.setMessage(String.format("'%s' ilanınıza %s kullanıcısından %.2f TL teklif geldi.", 
            post.getTitle(), bid.getUser().getUsername(), bid.getMiktar()));
        notification.setType(Notification.NotificationType.NEW_BID);
        notification.setUser(employer);
        notification.setRelatedPostId(post.getId());
        notification.setRelatedBidId(bid.getId());
        
        notificationRepository.save(notification);
        sendWebSocketNotification(employer.getId(), NotificationResponse.fromEntity(notification));
        log.info("Bid notification created for user: {} for post: {}", employer.getId(), post.getId());
    }

    @Transactional
    public void createBidAcceptedNotification(Bid bid) {
        User freelancer = bid.getUser();
        
        Notification notification = new Notification();
        notification.setTitle("Teklifiniz Kabul Edildi!");
        notification.setMessage(String.format("'%s' ilanı için verdiğiniz %.2f TL'lik teklif kabul edildi.", 
            bid.getPost().getTitle(), bid.getMiktar()));
        notification.setType(Notification.NotificationType.BID_ACCEPTED);
        notification.setUser(freelancer);
        notification.setRelatedPostId(bid.getPost().getId());
        notification.setRelatedBidId(bid.getId());
        
        notificationRepository.save(notification);
        sendWebSocketNotification(freelancer.getId(), NotificationResponse.fromEntity(notification));
        log.info("Bid accepted notification created for user: {}", freelancer.getId());
    }

    @Transactional
    public void createBidRejectedNotification(Bid bid) {
        User freelancer = bid.getUser();
        
        Notification notification = new Notification();
        notification.setTitle("Teklifiniz Reddedildi");
        notification.setMessage(String.format("'%s' ilanı için verdiğiniz teklif maalesef reddedildi.", 
            bid.getPost().getTitle()));
        notification.setType(Notification.NotificationType.BID_REJECTED);
        notification.setUser(freelancer);
        notification.setRelatedPostId(bid.getPost().getId());
        notification.setRelatedBidId(bid.getId());
        
        notificationRepository.save(notification);
        sendWebSocketNotification(freelancer.getId(), NotificationResponse.fromEntity(notification));
        log.info("Bid rejected notification created for user: {}", freelancer.getId());
    }

    @Transactional
    public void createPostUpdateNotification(Post post) {
        log.info("Post update notification should be sent for post: {}", post.getId());
    }

    @Transactional
    public void createDeadlineReminderNotification(Post post) {
        User employer = post.getUser();
        
        Notification notification = new Notification();
        notification.setTitle("Deadline Yaklaşıyor");
        notification.setMessage(String.format("'%s' ilanınızın son tarihi yaklaşıyor. Son tarih: %s", 
            post.getTitle(), post.getDeadline()));
        notification.setType(Notification.NotificationType.DEADLINE_REMINDER);
        notification.setUser(employer);
        notification.setRelatedPostId(post.getId());
        
        notificationRepository.save(notification);
        sendWebSocketNotification(employer.getId(), NotificationResponse.fromEntity(notification));
        log.info("Deadline reminder notification created for post: {}", post.getId());
    }

    @Transactional
    public void createSystemNotification(Long userId, String title, String message) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(Notification.NotificationType.SYSTEM);
        notification.setUser(user);
        
        notificationRepository.save(notification);
        sendWebSocketNotification(userId, NotificationResponse.fromEntity(notification));
        log.info("System notification created for user: {}", userId);
    }

    public List<NotificationResponse> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<NotificationResponse> response = new ArrayList<>();
        for (Notification notification : notifications) {
            response.add(NotificationResponse.fromEntity(notification));
        }
        return response;
    }

    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findUnreadByUserId(userId);
        List<NotificationResponse> response = new ArrayList<>();
        for (Notification notification : notifications) {
            response.add(NotificationResponse.fromEntity(notification));
        }
        return response;
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.markAsReadById(notificationId, LocalDateTime.now());
        log.info("Notification marked as read: {}", notificationId);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId, LocalDateTime.now());
        log.info("All notifications marked as read for user: {}", userId);
    }

    public List<NotificationResponse> getNotificationsByType(Long userId, Notification.NotificationType type) {
        List<Notification> notifications = notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
        List<NotificationResponse> response = new ArrayList<>();
        for (Notification notification : notifications) {
            response.add(NotificationResponse.fromEntity(notification));
        }
        return response;
    }

    public List<NotificationResponse> getRecentNotifications(Long userId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Notification> notifications = notificationRepository.findRecentByUserId(userId, since);
        List<NotificationResponse> response = new ArrayList<>();
        for (Notification notification : notifications) {
            response.add(NotificationResponse.fromEntity(notification));
        }
        return response;
    }

    @Transactional
    public void deleteOldNotifications(Long userId, int days) {
        LocalDateTime before = LocalDateTime.now().minusDays(days);
        notificationRepository.deleteByUserIdAndCreatedAtBefore(userId, before);
        log.info("Old notifications deleted for user: {} before: {}", userId, before);
    }

    private void sendWebSocketNotification(Long userId, NotificationResponse notificationResponse) {
        try {
            messagingTemplate.convertAndSendToUser(
                userId.toString(), 
                "/queue/notifications", 
                notificationResponse
            );
            log.info("WebSocket notification sent to user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification to user: {}", userId, e);
        }
    }
}