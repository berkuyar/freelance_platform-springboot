package com.uyarberk.freelance_platformm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequest {
    private String title;
    private String description;
    private String category;
    private double budgetMin;
    private double budgetMax;
    private LocalDateTime deadline;
    private String skills;
    private String status; // "OPEN", "IN_PROGRESS", "COMPLETED"
}