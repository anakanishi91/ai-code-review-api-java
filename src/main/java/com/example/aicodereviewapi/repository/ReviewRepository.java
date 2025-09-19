package com.example.aicodereviewapi.repository;

import com.example.aicodereviewapi.entity.Review;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByUserIdAndCreatedAtBeforeOrderByCreatedAtDesc(
            String userId, LocalDateTime createdAt, Pageable pageable);

    List<Review> findByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(
            String userId, LocalDateTime createdAt, Pageable pageable);

    List<Review> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
}
