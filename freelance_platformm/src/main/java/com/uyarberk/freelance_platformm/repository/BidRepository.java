package com.uyarberk.freelance_platformm.repository;

import com.uyarberk.freelance_platformm.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    
    // Belirli bir post'a gelen tüm bid'ler
    List<Bid> findByPostId(Long postId);
    
    // Freelancer'ın kendi bid'leri
    List<Bid> findByUserId(Long userId);
    
    // Employer'ın post'larına gelen tüm bid'ler
    @Query("SELECT b FROM Bid b WHERE b.post.user.id = :employerId ORDER BY b.createdAt DESC")
    List<Bid> findBidsByEmployer(@Param("employerId") Long employerId);
    
    // Post'a gelen bid'leri status'e göre filtrele
    @Query("SELECT b FROM Bid b WHERE b.post.id = :postId AND b.status = :status ORDER BY b.createdAt DESC")
    List<Bid> findByPostIdAndStatus(@Param("postId") Long postId, @Param("status") Bid.Status status);
    
    // Aynı freelancer'ın aynı post'a bid verip vermediğini kontrol et
    @Query("SELECT COUNT(b) > 0 FROM Bid b WHERE b.post.id = :postId AND b.user.id = :userId")
    boolean existsByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
}
