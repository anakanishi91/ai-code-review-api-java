package com.example.aicodereviewapi.service.review.impl;

import com.example.aicodereviewapi.dto.review.PaginatedReviewsResponse;
import com.example.aicodereviewapi.dto.review.ReviewRequest;
import com.example.aicodereviewapi.dto.review.ReviewResponse;
import com.example.aicodereviewapi.entity.Review;
import com.example.aicodereviewapi.exception.OwnershipException;
import com.example.aicodereviewapi.exception.ResourceNotFoundException;
import com.example.aicodereviewapi.repository.ReviewRepository;
import com.example.aicodereviewapi.service.review.ReviewService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public ReviewResponse createReview(String userId, ReviewRequest request) {
        Review review =
                Review.builder()
                        .code(request.getCode())
                        .review(request.getReview())
                        .chatModelId(request.getChatModelId())
                        .programmingLanguage(request.getProgrammingLanguage())
                        .userId(userId)
                        .build();
        return ReviewResponse.fromEntity(reviewRepository.save(review));
    }

    @Override
    public ReviewResponse getReviewById(String userId, String id) {
        Review review =
                reviewRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        if (!review.getUserId().equals(userId)) {
            throw new OwnershipException("You do not have permission to read this item");
        }
        return ReviewResponse.fromEntity(review);
    }

    @Override
    public PaginatedReviewsResponse getReviewsByUserId(
            String userId, int limit, UUID startingAfter, UUID endingBefore) {

        List<Review> reviews;

        if (startingAfter != null) {
            Review review =
                    reviewRepository
                            .findById(startingAfter.toString())
                            .orElseThrow(
                                    () -> new ResourceNotFoundException("Review for starting_after not found"));
            LocalDateTime startingAfterCreatedAt = review.getCreatedAt();
            reviews =
                    reviewRepository.findByUserIdAndCreatedAtBeforeOrderByCreatedAtDesc(
                            userId, startingAfterCreatedAt, PageRequest.of(0, limit));

        } else if (endingBefore != null) {
            Review review =
                    reviewRepository
                            .findById(endingBefore.toString())
                            .orElseThrow(
                                    () -> new ResourceNotFoundException("Review for ending_before not found"));
            LocalDateTime endingBeforeCreatedAt = review.getCreatedAt();
            reviews =
                    reviewRepository.findByUserIdAndCreatedAtAfterOrderByCreatedAtAsc(
                            userId, endingBeforeCreatedAt, PageRequest.of(0, limit));
        } else {
            reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, limit));
        }

        boolean hasMore = reviews.size() > limit;
        if (hasMore) {
            reviews = reviews.subList(0, limit);
        }

        List<ReviewResponse> reviewsOut = reviews.stream().map(ReviewResponse::fromEntity).toList();

        return new PaginatedReviewsResponse(reviewsOut, hasMore);
    }

    @Override
    public void deleteReview(String userId, String id) {
        Review review =
                reviewRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        if (!review.getUserId().equals(userId)) {
            throw new OwnershipException("You do not have permission to delete this item");
        }
        reviewRepository.delete(review);
    }
}
