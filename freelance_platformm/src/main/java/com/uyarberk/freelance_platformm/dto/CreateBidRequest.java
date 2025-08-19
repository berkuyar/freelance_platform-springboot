package com.uyarberk.freelance_platformm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateBidRequest {
    
    @NotNull(message = "Post ID gereklidir")
    private Long postId;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Teklif miktarı pozitif olmalıdır")
    private double miktar;
    
    @Size(max = 1000, message = "Mesaj çok uzun")
    private String message;
}