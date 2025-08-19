package com.uyarberk.freelance_platformm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private Long id;
    private String content;
    private Long chatId;
    
    // Gönderen bilgileri
    private Long senderId;
    private String senderUsername;
    private String senderName;
    
    // Mesaj bilgileri
    private LocalDateTime sentAt;
    private boolean isRead;
    private LocalDateTime readAt;
    private String messageType;
    
    // Kullanıcının kendi mesajı mı kontrolü
    private boolean isOwnMessage;
}