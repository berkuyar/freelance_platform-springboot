package com.uyarberk.freelance_platformm.repository;

import com.uyarberk.freelance_platformm.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.sentAt DESC")
    Page<Message> findByChatIdOrderBySentAtDesc(@Param("chatId") Long chatId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.sentAt ASC")
    List<Message> findByChatIdOrderBySentAtAsc(@Param("chatId") Long chatId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat.id = :chatId AND m.sender.id != :userId AND m.isRead = false")
    long countUnreadMessagesByChatIdAndUserId(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND m.sender.id != :userId AND m.isRead = false")
    List<Message> findUnreadMessagesByChatIdAndUserId(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.sentAt DESC LIMIT 1")
    Message findLastMessageByChatId(@Param("chatId") Long chatId);
}