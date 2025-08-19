package com.uyarberk.freelance_platformm.controller;

import com.uyarberk.freelance_platformm.dto.CreatePostRequest;
import com.uyarberk.freelance_platformm.dto.PostResponse;
import com.uyarberk.freelance_platformm.dto.PostUpdateRequestDto;
import com.uyarberk.freelance_platformm.dto.PostUpdateResponseDto;
import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.service.PostService;
import com.uyarberk.freelance_platformm.service.PostLikeService;
import com.uyarberk.freelance_platformm.dto.PostLikeDto;
import com.uyarberk.freelance_platformm.dto.PostLikeSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;

    @PreAuthorize("hasRole('EMPLOYER')")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<PostResponse> createPost(
            @RequestPart("post") @Valid CreatePostRequest request,
            @RequestPart(value = "attachment", required = false) MultipartFile attachment,
            @AuthenticationPrincipal User user) {

               request.setAttachment(attachment);
             PostResponse response = postService.createPost(request, user.getId());
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(@AuthenticationPrincipal User user) {
        Long currentUserId = user != null ? user.getId() : null;
        List<PostResponse> list = postService.getAllPosts(currentUserId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Long currentUserId = user != null ? user.getId() : null;
        PostResponse response = postService.getPostById(id, currentUserId);
        return ResponseEntity.ok(response);
    }
    // employer kendi postunu günceller.
    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/{id}")
    public ResponseEntity<PostUpdateResponseDto> updatePost(@PathVariable Long id, @Valid @RequestBody PostUpdateRequestDto postUpdateRequestDto, @AuthenticationPrincipal User user) {
        Long userId = user.getId();

        PostUpdateResponseDto dto = postService.updatePost(id, postUpdateRequestDto, userId);
        return ResponseEntity.ok(dto);

    }
    // employer kendi postlarını getirir.
    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/my")
    public ResponseEntity<List<PostResponse>> getMyPosts(@AuthenticationPrincipal User user) {
        long userId = user.getId();

        List<PostResponse> list = postService.getMyPosts(userId);
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/my/filter")
    public ResponseEntity<List<PostResponse>> getMyPostsFilter(
            @RequestParam(value = "budgetMin", required = false) Double budgetMin,
            @RequestParam(value = "budgetMax", required = false) Double budgetMax,
            @AuthenticationPrincipal User user
    ){
        Long userId =  user.getId();

        List<PostResponse> list = postService.getMyPostsFilter(budgetMin, budgetMax, userId);
        return ResponseEntity.ok(list);

    }

    @PreAuthorize("hasRole('EMPLOYER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id, @AuthenticationPrincipal User user){

        Long userId =  user.getId();
          postService.deletePost(id, userId);
          return ResponseEntity.ok("Deleted");

    }

    // ============= LIKE ENDPOINTS =============
    
    @PostMapping("/{id}/like")
    public ResponseEntity<String> likePost(@PathVariable Long id, @AuthenticationPrincipal User user) {
        postLikeService.likePost(user.getId(), id);
        return ResponseEntity.ok("Post beğenildi");
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<String> unlikePost(@PathVariable Long id, @AuthenticationPrincipal User user) {
        postLikeService.unlikePost(user.getId(), id);
        return ResponseEntity.ok("Beğeni kaldırıldı");
    }

    @GetMapping("/{id}/likes")
    public ResponseEntity<List<PostLikeDto>> getPostLikes(@PathVariable Long id) {
        List<PostLikeDto> likes = postLikeService.getPostLikes(id);
        return ResponseEntity.ok(likes);
    }

    @GetMapping("/{id}/likes/summary")
    public ResponseEntity<PostLikeSummaryDto> getPostLikeSummary(@PathVariable Long id, @AuthenticationPrincipal User user) {
        PostLikeSummaryDto summary = postLikeService.getPostLikeSummary(id, user.getId());
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{id}/likes/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable Long id) {
        long count = postLikeService.getLikeCount(id);
        return ResponseEntity.ok(count);
    }
 }
