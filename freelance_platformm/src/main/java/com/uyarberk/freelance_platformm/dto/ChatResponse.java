package com.uyarberk.freelance_platformm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    private Long id;
    private Long bidId;
    private String postTitle;
    
    // Employer bilgileri
    private Long employerId;
    private String employerUsername;
    private String employerName;
    
    // Freelancer bilgileri
    private Long freelancerId;
    private String freelancerUsername;
    private String freelancerName;
    
    // Chat bilgileri
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private boolean isActive;
    
    // Son mesaj bilgisi
    private String lastMessageContent;
    private LocalDateTime lastMessageTime;
    private String lastMessageSender;
    
    // Okunmamış mesaj sayısı
    private long unreadCount;
}