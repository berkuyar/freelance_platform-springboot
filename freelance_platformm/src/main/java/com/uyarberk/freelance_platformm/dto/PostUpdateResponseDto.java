package com.uyarberk.freelance_platformm.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateResponseDto {

    private Long id;
    private String title;
    private String description;
    private String category;
    private double budgetMin;
    private double budgetMax;
    private LocalDateTime deadline;
    private String skills;
    private String status;
    private String attachmentName;
    private String attachmentPath;
    private LocalDateTime createdAt;

    // User bilgileri
    private Long userId;
    private String username;
    private String employerName; // name + surname

}
