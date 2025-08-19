package com.uyarberk.freelance_platformm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Başlık gereklidir")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Açıklama gereklidir")
    @Size(max = 2000)
    private String description;

    @NotBlank(message = "Kategori gereklidir")
    private String category;

    @DecimalMin(value = "0.0", inclusive = false)
    private double budgetMin;

    @DecimalMin(value = "0.0", inclusive = false)
    private double budgetMax;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime deadline;

    @Column(length = 1000)
    private String skills; // Virgülle ayrılmış: "Java,Spring,React"

    @Column(length = 500)
    private String attachmentPath; // Dosya yolu

    @Column(length = 100)
    private String attachmentName; // Dosya adı

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Status {
        OPEN,
        IN_PROGRESS,
        COMPLETED
    }

}
