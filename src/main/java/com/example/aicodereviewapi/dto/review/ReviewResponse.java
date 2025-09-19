package com.example.aicodereviewapi.dto.review;

import com.example.aicodereviewapi.entity.Review;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private UUID id;
    private String code;
    private String chatModelId;
    private String programmingLanguage;
    private String review;
    private LocalDateTime createdAt;

    public static ReviewResponse fromEntity(Review review) {
        return ReviewResponse.builder()
                .id(UUID.fromString(review.getId()))
                .code(review.getCode())
                .review(review.getReview())
                .programmingLanguage(review.getProgrammingLanguage())
                .chatModelId(review.getChatModelId())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
