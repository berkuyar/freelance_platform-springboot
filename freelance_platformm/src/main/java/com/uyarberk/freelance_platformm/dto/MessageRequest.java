package com.uyarberk.freelance_platformm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    @NotNull(message = "Chat ID gereklidir")
    private Long chatId;

    @NotBlank(message = "Mesaj içeriği gereklidir")
    @Size(max = 2000, message = "Mesaj 2000 karakteri geçemez")
    private String content;

    private String messageType = "TEXT"; // TEXT, FILE, SYSTEM
}