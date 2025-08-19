package com.uyarberk.freelance_platformm.service;

import com.uyarberk.freelance_platformm.dto.CreatePostRequest;
import com.uyarberk.freelance_platformm.dto.PostResponse;
import com.uyarberk.freelance_platformm.dto.PostUpdateRequestDto;
import com.uyarberk.freelance_platformm.dto.PostUpdateResponseDto;
import com.uyarberk.freelance_platformm.exception.PostNotFoundException;
import com.uyarberk.freelance_platformm.model.Post;
import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.repository.PostRepository;
import com.uyarberk.freelance_platformm.repository.UserRepository;
import com.uyarberk.freelance_platformm.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;

    public PostResponse createPost(CreatePostRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setCategory(request.getCategory());
        post.setBudgetMin(request.getBudgetMin());
        post.setBudgetMax(request.getBudgetMax());
        post.setDeadline(request.getDeadline());
        post.setSkills(request.getSkills());
        post.setUser(user);
        post.setStatus(Post.Status.OPEN);
        
        // Dosya upload işlemi
        if (request.getAttachment() != null && !request.getAttachment().isEmpty()) {
            String fileName = saveFile(request.getAttachment());
            post.setAttachmentName(request.getAttachment().getOriginalFilename());
            post.setAttachmentPath(fileName);
        }
        
        Post savedPost = postRepository.save(post);
        return convertToResponse(savedPost);
    }
    
    private String saveFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/posts");
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Dosya yüklenemedi: " + e.getMessage());
        }
    }
    
    private PostResponse convertToResponse(Post post) {
        return convertToResponse(post, null);
    }
    
    private PostResponse convertToResponse(Post post, Long currentUserId) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setDescription(post.getDescription());
        response.setCategory(post.getCategory());
        response.setBudgetMin(post.getBudgetMin());
        response.setBudgetMax(post.getBudgetMax());
        response.setDeadline(post.getDeadline());
        response.setSkills(post.getSkills());
        response.setStatus(post.getStatus().toString());
        response.setAttachmentName(post.getAttachmentName());
        response.setAttachmentPath(post.getAttachmentPath() != null ? "/api/files/" + post.getAttachmentPath() : null);
        response.setCreatedAt(post.getCreatedAt());
        response.setUserId(post.getUser().getId());
        response.setUsername(post.getUser().getUsername());
        response.setEmployerName(post.getUser().getName() + " " + post.getUser().getSurname());
        
        // Like bilgileri
        response.setLikeCount(postLikeRepository.countByPost(post));
        if (currentUserId != null) {
            User currentUser = userRepository.findById(currentUserId).orElse(null);
            response.setLikedByCurrentUser(currentUser != null && 
                postLikeRepository.existsByUserAndPost(currentUser, post));
        } else {
            response.setLikedByCurrentUser(false);
        }
        
        return response;
    }

    public List<PostResponse> getAllPosts(){
        return getAllPosts(null);
    }
    
    public List<PostResponse> getAllPosts(Long currentUserId){
        List<Post> posts = postRepository.findAll();
        List<PostResponse> postResponses = new ArrayList<>();

        for(Post post : posts){
            PostResponse postResponse = convertToResponse(post, currentUserId);
            postResponses.add(postResponse);
        }
        return postResponses;
    }

    public PostResponse getPostById(Long id){
        return getPostById(id, null);
    }
    
    public PostResponse getPostById(Long id, Long currentUserId){
        Post post = postRepository.findById(id).orElseThrow(() ->
                new PostNotFoundException("İlan bulunamadı."));
        return convertToResponse(post, currentUserId);
    }

    public PostUpdateResponseDto updatePost(Long id, PostUpdateRequestDto postUpdateRequestDto, Long userId) {

        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("İlan bulunamadı."));
        
        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bu ilanı güncelleme yetkiniz yok");
        }
        
        post.setTitle(postUpdateRequestDto.getTitle());
        post.setDescription(postUpdateRequestDto.getDescription());
        post.setCategory(postUpdateRequestDto.getCategory());
        post.setBudgetMin(postUpdateRequestDto.getBudgetMin());
        post.setBudgetMax(postUpdateRequestDto.getBudgetMax());
        post.setDeadline(postUpdateRequestDto.getDeadline());
        post.setSkills(postUpdateRequestDto.getSkills());

        postRepository.save(post);

        PostUpdateResponseDto postUpdateResponseDto = new PostUpdateResponseDto();
        postUpdateResponseDto.setId(post.getId());
        postUpdateResponseDto.setTitle(postUpdateRequestDto.getTitle());
        postUpdateResponseDto.setDescription(postUpdateRequestDto.getDescription());
        postUpdateResponseDto.setCategory(postUpdateRequestDto.getCategory());
        postUpdateResponseDto.setBudgetMin(postUpdateRequestDto.getBudgetMin());
        postUpdateResponseDto.setBudgetMax(postUpdateRequestDto.getBudgetMax());
        postUpdateResponseDto.setDeadline(postUpdateRequestDto.getDeadline());
        postUpdateResponseDto.setSkills(postUpdateRequestDto.getSkills());
        postUpdateResponseDto.setUserId(userId);
        postUpdateResponseDto.setUsername(post.getUser().getUsername());
        postUpdateResponseDto.setEmployerName(post.getUser().getName() + " " + post.getUser().getSurname());
        return postUpdateResponseDto;
    }

    public List<PostResponse> getMyPosts (Long userId){
        List<Post> posts = postRepository.findByUserId(userId);
        if(posts.isEmpty()){
            log.info("Bu kullanıcıya ait post yok.");
           return new ArrayList<>();
        }
        List<PostResponse> postResponses = new ArrayList<>();
        for(Post post : posts){
            PostResponse postResponse = convertToResponse(post, userId);
            postResponses.add(postResponse);
        }
         return postResponses;
    }

    public List<PostResponse> getMyPostsFilter (Double budgetMin, Double budgetMax, Long userId){
        List<Post> posts = postRepository.findMyPostsWithFilters(
                budgetMin, budgetMax, userId
        );
        if(posts.isEmpty()){
            return new ArrayList<>();
        }
        List<PostResponse> postResponses = new ArrayList<>();
        for(Post post : posts){
            PostResponse postResponse = convertToResponse(post, userId);
            postResponses.add(postResponse);
        }
        return postResponses;
    }


    public boolean deletePost(Long id, Long userId){
Post post = postRepository.findById(id).orElseThrow(()-> new PostNotFoundException("Silinecek post bulunamadı."));
   if(!post.getUser().getId().equals(userId)){
     throw new RuntimeException("Bu randevuyu silme yetkiniz yok.");
}
   postRepository.delete(post);
   return true;
    }
}