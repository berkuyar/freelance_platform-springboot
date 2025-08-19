package com.uyarberk.freelance_platformm.repository;

import com.uyarberk.freelance_platformm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.user.id = :userId")
    Long countPostsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(b) FROM Bid b WHERE b.user.id = :userId")
    Long countBidsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(b) FROM Bid b WHERE b.user.id = :userId AND b.status = 'ACCEPTED'")
    Long countAcceptedBidsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(b) FROM Bid b WHERE b.user.id = :userId AND b.status = 'REJECTED'")
    Long countRejectedBidsByUserId(@Param("userId") Long userId);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false")
    List<User> findActiveUsers();

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.isDeleted = false")
    Optional<User> findActiveById(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.isDeleted = false")
    Optional<User> findActiveByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isDeleted = false")
    Optional<User> findActiveByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.isDeleted = true")
    List<User> findDeletedUsers();
}