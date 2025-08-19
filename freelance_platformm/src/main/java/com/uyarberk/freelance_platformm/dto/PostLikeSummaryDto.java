package com.uyarberk.freelance_platformm.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeSummaryDto {

    private Long postId;
    private Long likeCount;
    private boolean isLikedByCurrentUser;
    private List<PostLikeDto> likes;
}
