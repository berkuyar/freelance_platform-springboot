package com.uyarberk.freelance_platformm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeDto {

    private Long id;
    private Long userId;
    private String username;
    private String name;
    private String surname;
    private LocalDateTime createdAt;

}
