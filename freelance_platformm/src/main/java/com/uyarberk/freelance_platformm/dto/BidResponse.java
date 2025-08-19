package com.uyarberk.freelance_platformm.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BidResponse {
    
    private Long id;
    private double miktar;
    private String message;
    private String status;
    private LocalDateTime createdAt;
    
    // Post bilgileri
    private Long postId;
    private String postTitle;
    
    // Freelancer bilgileri
    private Long freelancerId;
    private String freelancerUsername;
    private String freelancerName;
}