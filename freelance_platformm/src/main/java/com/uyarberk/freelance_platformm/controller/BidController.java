package com.uyarberk.freelance_platformm.controller;

import com.uyarberk.freelance_platformm.dto.BidResponse;
import com.uyarberk.freelance_platformm.dto.CreateBidRequest;
import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.service.BidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
public class BidController {
    
    private final BidService bidService;
    
    @PreAuthorize("hasRole('FREELANCER')")
    @PostMapping
    public ResponseEntity<BidResponse> createBid(
            @Valid @RequestBody CreateBidRequest request,
            @AuthenticationPrincipal User user) {
        
        BidResponse response = bidService.createBid(request, user.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    // Employer belirli bir post'a gelen bid'leri görür
    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<BidResponse>> getBidsByPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user) {
        
        List<BidResponse> bids = bidService.getBidsByPost(postId, user.getId());
        return ResponseEntity.ok(bids);
    }
    
    // Freelancer kendi bid'lerini görür
    @PreAuthorize("hasRole('FREELANCER')")
    @GetMapping("/my")
    public ResponseEntity<List<BidResponse>> getMyBids(
            @AuthenticationPrincipal User user) {
        
        List<BidResponse> bids = bidService.getMyBids(user.getId());
        return ResponseEntity.ok(bids);
    }
    
    // Employer tüm post'larına gelen bid'leri görür
    @PreAuthorize("hasRole('EMPLOYER')")
    @GetMapping("/my-posts")
    public ResponseEntity<List<BidResponse>> getAllMyPostsBids(
            @AuthenticationPrincipal User user) {
        
        List<BidResponse> bids = bidService.getAllMyPostsBids(user.getId());
        return ResponseEntity.ok(bids);
    }
    
    // Employer bid'i kabul eder
    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/{bidId}/accept")
    public ResponseEntity<String> acceptBid(
            @PathVariable Long bidId,
            @AuthenticationPrincipal User user) {
        
        String message = bidService.acceptBid(bidId, user.getId());
        return ResponseEntity.ok(message);
    }
    
    // Employer bid'i red eder
    @PreAuthorize("hasRole('EMPLOYER')")
    @PutMapping("/{bidId}/reject")
    public ResponseEntity<String> rejectBid(
            @PathVariable Long bidId,
            @AuthenticationPrincipal User user) {
        
        String message = bidService.rejectBid(bidId, user.getId());
        return ResponseEntity.ok(message);
    }
}
