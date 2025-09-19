package com.example.aicodereviewapi.controller;

import static com.example.aicodereviewapi.config.OpenApiConfig.SECURITY_SCHEME_NAME;

import com.example.aicodereviewapi.dto.review.PaginatedReviewsResponse;
import com.example.aicodereviewapi.dto.review.ReviewRequest;
import com.example.aicodereviewapi.dto.review.ReviewResponse;
import com.example.aicodereviewapi.dto.review.ReviewWithAiRequest;
import com.example.aicodereviewapi.security.JwtUserDetails;
import com.example.aicodereviewapi.service.openai.OpenaiService;
import com.example.aicodereviewapi.service.review.ReviewService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@SecurityRequirement(name = SECURITY_SCHEME_NAME)
public class ReviewController {

    private final ReviewService reviewService;

    private final OpenaiService openaiService;

    @PostMapping("/")
    public ReviewResponse createReview(
            @Valid @RequestBody ReviewRequest request, @AuthenticationPrincipal JwtUserDetails user) {
        return reviewService.createReview(user.getId(), request);
    }

    @GetMapping("/{id}")
    public ReviewResponse getReviewById(
            @PathVariable String id, @AuthenticationPrincipal JwtUserDetails user) {
        return reviewService.getReviewById(user.getId(), id);
    }

    @GetMapping("/")
    public PaginatedReviewsResponse getReviewsByUser(
            @RequestParam(defaultValue = "10") @Max(100) @Min(1) int limit,
            @RequestParam(name = "starting_after", required = false) UUID startingAfter,
            @RequestParam(name = "ending_before", required = false) UUID endingBefore,
            @AuthenticationPrincipal JwtUserDetails user) {
        if (startingAfter != null && endingBefore != null) {
            throw new IllegalArgumentException("Cannot use both starting_after and ending_before");
        }

        return reviewService.getReviewsByUserId(user.getId(), limit + 1, startingAfter, endingBefore);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable String id, @AuthenticationPrincipal JwtUserDetails user) {
        String userId = user.getId();
        reviewService.deleteReview(userId, id);
    }

    @PostMapping("/ai")
    public String createReviewWithAi(
            @RequestBody ReviewWithAiRequest request, @AuthenticationPrincipal JwtUserDetails user) {
        String review = openaiService.chat(request.getCode(), request.getProgrammingLanguage());
        reviewService.createReview(
                user.getId(),
                new ReviewRequest(
                        request.getId(),
                        request.getCode(),
                        request.getChatModelId(),
                        request.getProgrammingLanguage(),
                        review));
        return review;
    }
}
