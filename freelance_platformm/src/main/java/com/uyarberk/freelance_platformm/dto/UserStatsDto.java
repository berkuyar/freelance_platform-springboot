package com.uyarberk.freelance_platformm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDto {

    private Long totalPosts;
    private Long totalBids;
    private Long acceptedBids;
    private Long rejectedBids;
    private Long pendingBids;
    private Double successRate;





}
