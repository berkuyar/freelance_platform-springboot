package com.uyarberk.freelance_platformm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    private Long postId;
    private Long userId;
    private String username;
    private String userFullName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}