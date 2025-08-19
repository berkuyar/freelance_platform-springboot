package com.uyarberk.freelance_platformm.repository;

import com.uyarberk.freelance_platformm.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.isDeleted = false ORDER BY c.createdAt DESC")
    List<Comment> findByPostIdAndIsDeletedFalseOrderByCreatedAtDesc(@Param("postId") Long postId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId AND c.isDeleted = false")
    long countByPostIdAndIsDeletedFalse(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId AND c.isDeleted = false ORDER BY c.createdAt DESC")
    List<Comment> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(@Param("userId") Long userId);
}