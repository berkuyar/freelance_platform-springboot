package com.uyarberk.freelance_platformm.repository;

import com.uyarberk.freelance_platformm.model.Post;
import com.uyarberk.freelance_platformm.model.PostLike;
import com.uyarberk.freelance_platformm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByUserAndPost(User user, Post post);
    
    Optional<PostLike> findByUserAndPost(User user, Post post);
    
    long countByPost(Post post);
    
    void deleteByUserAndPost(User user, Post post);
    
    List<PostLike> findByPost(Post post);
    
    List<PostLike> findByUser(User user);
}