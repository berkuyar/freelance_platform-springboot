package com.uyarberk.freelance_platformm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateBidRequest {
    
    @NotNull(message = "Post ID is required")
    private Long postId;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Bid amount must be positive")
    private double miktar;
    
    @Size(max = 1000, message = "Message too long")
    private String message;
}