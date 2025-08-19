package com.uyarberk.freelance_platformm.repository;

import com.uyarberk.freelance_platformm.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE c.bid.id = :bidId")
    Optional<Chat> findByBidId(@Param("bidId") Long bidId);

    @Query("SELECT c FROM Chat c WHERE (c.employer.id = :userId OR c.freelancer.id = :userId) AND c.isActive = true ORDER BY c.lastMessageAt DESC")
    List<Chat> findUserChats(@Param("userId") Long userId);

    @Query("SELECT c FROM Chat c WHERE c.id = :chatId AND (c.employer.id = :userId OR c.freelancer.id = :userId)")
    Optional<Chat> findByIdAndUserId(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM Chat c WHERE (c.employer.id = :userId OR c.freelancer.id = :userId) AND c.isActive = true")
    long countActiveChatsByUserId(@Param("userId") Long userId);
}