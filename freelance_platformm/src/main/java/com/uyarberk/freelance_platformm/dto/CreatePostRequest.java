package com.uyarberk.freelance_platformm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {
    
    @NotBlank(message = "Başlık gereklidir")
    @Size(max = 255, message = "Başlık 255 karakterden uzun olamaz")
    private String title;
    
    @NotBlank(message = "Açıklama gereklidir")
    @Size(max = 2000, message = "Açıklama 2000 karakterden uzun olamaz")
    private String description;
    
    @NotBlank(message = "Kategori gereklidir")
    private String category;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum bütçe 0'dan büyük olmalıdır")
    private double budgetMin;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Maksimum bütçe 0'dan büyük olmalıdır")
    private double budgetMax;
    
    private LocalDateTime deadline;
    
    @Size(max = 1000, message = "Yetenekler 1000 karakterden uzun olamaz")
    private String skills;
    
    private MultipartFile attachment;
}