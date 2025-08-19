package com.uyarberk.freelance_platformm.service;


import com.uyarberk.freelance_platformm.exception.PostNotFoundException;
import com.uyarberk.freelance_platformm.exception.UserNotFoundException;
import com.uyarberk.freelance_platformm.model.Post;
import com.uyarberk.freelance_platformm.model.PostLike;
import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.repository.PostLikeRepository;
import com.uyarberk.freelance_platformm.repository.PostRepository;
import com.uyarberk.freelance_platformm.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import com.uyarberk.freelance_platformm.dto.PostLikeDto;
import com.uyarberk.freelance_platformm.dto.PostLikeSummaryDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void likePost (long userId, long postId) {

        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("Kullanıcı bulunamadı"));

        Post post = postRepository.findById(postId).orElseThrow(()
                -> new PostNotFoundException("Post bulunamadı"));

     if(postLikeRepository.existsByUserAndPost(user, post)){
         throw new RuntimeException("Post zaten beğenilmiş");
     }
     if(post.getUser().getId().equals(userId)){
         throw new RuntimeException("Kendi postunu beğenemezsin");
     }
          PostLike postLike = new PostLike();
          postLike.setUser(user);
          postLike.setPost(post);
          postLikeRepository.save(postLike);
    }

       public void unlikePost(long userId, long postId) {

           User user = userRepository.findById(userId).orElseThrow(()
                   -> new UserNotFoundException("Kullanıcı bulunamadı"));

           Post post = postRepository.findById(postId).orElseThrow(()
                   -> new PostNotFoundException("Post bulunamadı"));

        if(!postLikeRepository.existsByUserAndPost(user, post)){
            throw new RuntimeException("Post zaten beğenilmemiş");
        }
        postLikeRepository.deleteByUserAndPost(user, post);

}

    public PostLikeSummaryDto getPostLikeSummary(long postId, long currentUserId) {
        Post post = postRepository.findById(postId).orElseThrow(()
                -> new PostNotFoundException("Post bulunamadı"));

        User currentUser = userRepository.findById(currentUserId).orElseThrow(()
                -> new UserNotFoundException("Kullanıcı bulunamadı"));

        long likeCount = postLikeRepository.countByPost(post);
        boolean isLikedByCurrentUser = postLikeRepository.existsByUserAndPost(currentUser, post);
        
        List<PostLike> postLikes = postLikeRepository.findByPost(post);
        List<PostLikeDto> likes = postLikes.stream()
                .map(this::convertToPostLikeDto)
                .collect(Collectors.toList());

        return new PostLikeSummaryDto(postId, likeCount, isLikedByCurrentUser, likes);
    }

    public List<PostLikeDto> getPostLikes(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()
                -> new PostNotFoundException("Post bulunamadı"));

        List<PostLike> postLikes = postLikeRepository.findByPost(post);
        return postLikes.stream()
                .map(this::convertToPostLikeDto)
                .collect(Collectors.toList());
    }

    public long getLikeCount(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()
                -> new PostNotFoundException("Post bulunamadı"));
        
        return postLikeRepository.countByPost(post);
    }

    public boolean isPostLikedByUser(long userId, long postId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("Kullanıcı bulunamadı"));
        
        Post post = postRepository.findById(postId).orElseThrow(()
                -> new PostNotFoundException("Post bulunamadı"));

        return postLikeRepository.existsByUserAndPost(user, post);
    }

    private PostLikeDto convertToPostLikeDto(PostLike postLike) {
        User user = postLike.getUser();
        return new PostLikeDto(
                postLike.getId(),
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getSurname(),
                postLike.getCreatedAt()
        );
    }
}
