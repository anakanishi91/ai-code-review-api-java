package com.example.aicodereviewapi.service.review;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.aicodereviewapi.dto.review.PaginatedReviewsResponse;
import com.example.aicodereviewapi.dto.review.ReviewRequest;
import com.example.aicodereviewapi.dto.review.ReviewResponse;
import com.example.aicodereviewapi.entity.Review;
import com.example.aicodereviewapi.exception.OwnershipException;
import com.example.aicodereviewapi.exception.ResourceNotFoundException;
import com.example.aicodereviewapi.repository.ReviewRepository;
import com.example.aicodereviewapi.service.review.impl.ReviewServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock private ReviewRepository reviewRepository;

    @InjectMocks private ReviewServiceImpl reviewService;

    private Review review;
    private ReviewRequest reviewRequest;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();

        UUID reviewId = UUID.randomUUID();
        String code = "some code";
        String lang = "Java";
        String chatModelId = "gpt-4";
        String reviewContent = "nice review";
        reviewRequest = new ReviewRequest(reviewId, code, lang, chatModelId, reviewContent);

        review =
                Review.builder()
                        .id(reviewId.toString())
                        .userId(userId)
                        .code(code)
                        .review(reviewContent)
                        .chatModelId(chatModelId)
                        .programmingLanguage(lang)
                        .createdAt(LocalDateTime.now())
                        .build();
    }

    @Test
    void shouldSaveAndReturnReviewWhenCreateReview() {
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewResponse response = reviewService.createReview(userId, reviewRequest);

        assertNotNull(response);
        assertEquals(review.getCode(), response.getCode());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void shouldReturnReviewWhenGetReviewByIdOwnedByUser() {
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

        ReviewResponse response = reviewService.getReviewById(userId, review.getId());

        assertEquals(review.getId(), response.getId().toString());
        verify(reviewRepository).findById(review.getId());
    }

    @Test
    void shouldThrowOwnershipExceptionWhenGetReviewByIdNotOwned() {
        String otherUserId = UUID.randomUUID().toString();
        String otherReviewId = UUID.randomUUID().toString();
        Review otherUserReview = Review.builder().id(otherReviewId).userId(otherUserId).build();
        when(reviewRepository.findById(otherReviewId)).thenReturn(Optional.of(otherUserReview));

        OwnershipException ex =
                assertThrows(
                        OwnershipException.class, () -> reviewService.getReviewById(userId, otherReviewId));

        assertEquals("You do not have permission to read this item", ex.getMessage());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenGetReviewByIdNotFound() {
        when(reviewRepository.findById("unknown")).thenReturn(Optional.empty());

        ResourceNotFoundException ex =
                assertThrows(
                        ResourceNotFoundException.class, () -> reviewService.getReviewById(userId, "unknown"));

        assertEquals("Review not found", ex.getMessage());
    }

    @Test
    void shouldReturnPaginatedReviewsWhenGetReviewsByUserId() {
        List<Review> reviews = List.of(review);
        when(reviewRepository.findByUserIdOrderByCreatedAtDesc(eq(userId), any(PageRequest.class)))
                .thenReturn(reviews);

        PaginatedReviewsResponse response = reviewService.getReviewsByUserId(userId, 10, null, null);

        assertEquals(1, response.getReviews().size());
        assertFalse(response.isHasMore());
        verify(reviewRepository).findByUserIdOrderByCreatedAtDesc(eq(userId), any(PageRequest.class));
    }

    @Test
    void shouldDeleteReviewWhenDeleteReviewOwnedByUser() {
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

        reviewService.deleteReview(userId, review.getId());

        verify(reviewRepository).delete(review);
    }

    @Test
    void shouldThrowOwnershipExceptionWhenDeleteReviewNotOwned() {
        String otherUserId = UUID.randomUUID().toString();
        String otherReviewId = UUID.randomUUID().toString();
        Review otherReview = Review.builder().id(otherReviewId).userId(otherUserId).build();
        when(reviewRepository.findById(otherReviewId)).thenReturn(Optional.of(otherReview));

        OwnershipException ex =
                assertThrows(
                        OwnershipException.class, () -> reviewService.deleteReview(userId, otherReviewId));

        assertEquals("You do not have permission to delete this item", ex.getMessage());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDeleteReviewNotFound() {
        when(reviewRepository.findById("unknown")).thenReturn(Optional.empty());

        ResourceNotFoundException ex =
                assertThrows(
                        ResourceNotFoundException.class, () -> reviewService.deleteReview(userId, "unknown"));

        assertEquals("Review not found with id: unknown", ex.getMessage());
    }
}
