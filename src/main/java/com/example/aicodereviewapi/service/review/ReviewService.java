package com.example.aicodereviewapi.service.review;

import com.example.aicodereviewapi.dto.review.PaginatedReviewsResponse;
import com.example.aicodereviewapi.dto.review.ReviewRequest;
import com.example.aicodereviewapi.dto.review.ReviewResponse;
import java.util.UUID;

public interface ReviewService {
    ReviewResponse createReview(String userId, ReviewRequest request);

    ReviewResponse getReviewById(String userId, String id);

    PaginatedReviewsResponse getReviewsByUserId(
            String userId, int limit, UUID startingAfter, UUID endingBefore);

    void deleteReview(String userId, String id);
}
