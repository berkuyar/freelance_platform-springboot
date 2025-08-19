package com.uyarberk.freelance_platformm.service;

import com.uyarberk.freelance_platformm.dto.CommentResponse;
import com.uyarberk.freelance_platformm.dto.CreateCommentRequest;
import com.uyarberk.freelance_platformm.dto.UpdateCommentRequest;
import com.uyarberk.freelance_platformm.exception.CommentNotFoundException;
import com.uyarberk.freelance_platformm.exception.PostNotFoundException;
import com.uyarberk.freelance_platformm.exception.UserNotFoundException;
import com.uyarberk.freelance_platformm.model.Comment;
import com.uyarberk.freelance_platformm.model.Post;
import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.repository.CommentRepository;
import com.uyarberk.freelance_platformm.repository.PostRepository;
import com.uyarberk.freelance_platformm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse createComment(Long postId, CreateCommentRequest request, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post bulunamadı. ID: " + postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı. ID: " + userId));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setPost(post);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);
        return mapToCommentResponse(savedComment);
    }

    public List<CommentResponse> getPostComments(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post bulunamadı. ID: " + postId);
        }

        List<Comment> comments = commentRepository.findByPostIdAndIsDeletedFalseOrderByCreatedAtDesc(postId);
        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    public long getCommentCount(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post bulunamadı. ID: " + postId);
        }

        return commentRepository.countByPostIdAndIsDeletedFalse(postId);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, UpdateCommentRequest request, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Yorum bulunamadı. ID: " + commentId));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Sadece kendi yorumlarınızı güncelleyebilirsiniz");
        }

        if (comment.isDeleted()) {
            throw new CommentNotFoundException("Yorum silinmiş");
        }

        comment.setContent(request.getContent());
        Comment updatedComment = commentRepository.save(comment);
        return mapToCommentResponse(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Yorum bulunamadı. ID: " + commentId));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Sadece kendi yorumlarınızı silebilirsiniz");
        }

        comment.softDelete();
        commentRepository.save(comment);
    }

    public List<CommentResponse> getUserComments(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Kullanıcı bulunamadı. ID: " + userId);
        }

        List<Comment> comments = commentRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);
        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setPostId(comment.getPost().getId());
        response.setUserId(comment.getUser().getId());
        response.setUsername(comment.getUser().getUsername());
        response.setUserFullName(comment.getUser().getName() + " " + comment.getUser().getSurname());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        return response;
    }
}