package com.uyarberk.freelance_platformm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentRequest {

    @NotBlank(message = "İçerik gereklidir")
    @Size(max = 1000, message = "İçerik 1000 karakteri geçemez")
    private String content;
}