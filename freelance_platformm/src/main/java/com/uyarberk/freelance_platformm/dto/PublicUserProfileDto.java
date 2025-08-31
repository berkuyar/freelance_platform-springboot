package com.uyarberk.freelance_platformm.dto;

import com.uyarberk.freelance_platformm.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicUserProfileDto {

    private Long id;
    private String username;
    private String name;
    private String surname;
    private String bio;
    private String city;
    private String skills;
    private User.Role role;
    private LocalDateTime createdAt;

}