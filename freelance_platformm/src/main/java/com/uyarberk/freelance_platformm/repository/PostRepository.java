package com.uyarberk.freelance_platformm.repository;

import com.uyarberk.freelance_platformm.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // İşverene göre iş ilanları
    List<Post> findByUserId(Long userId);

    // Status'e göre filtreleme
    List<Post> findByStatus(Post.Status status);

    // Kategori'ye göre filtreleme
    List<Post> findByCategory(String category);

    // Bütçe aralığına göre filtreleme
    @Query("SELECT p FROM Post p WHERE p.budgetMin <= :maxBudget AND p.budgetMax >= :minBudget")
    List<Post> findByBudgetRange(@Param("minBudget") double minBudget, @Param("maxBudget") double maxBudget);

    // Skills'de arama (skills field'ında geçen)
    @Query("SELECT p FROM Post p WHERE p.skills LIKE %:skill%")
    List<Post> findBySkillsContaining(@Param("skill") String skill);

    // Başlık ve açıklama'da arama
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.description LIKE %:keyword%")
    List<Post> findByTitleOrDescriptionContaining(@Param("keyword") String keyword);

    // Açık iş ilanları (OPEN status)
    @Query("SELECT p FROM Post p WHERE p.status = 'OPEN' ORDER BY p.createdAt DESC")
    List<Post> findOpenPosts();

    // Kullanıcının kendi ilanlarını filtreleme
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId " +
           "AND (:budgetMin IS NULL OR p.budgetMin >= :budgetMin) " +
           "AND (:budgetMax IS NULL OR p.budgetMax <= :budgetMax) " +
           "ORDER BY p.createdAt DESC")
    List<Post> findMyPostsWithFilters(@Param("budgetMin") Double budgetMin, 
                                    @Param("budgetMax") Double budgetMax, 
                                    @Param("userId") Long userId);
}