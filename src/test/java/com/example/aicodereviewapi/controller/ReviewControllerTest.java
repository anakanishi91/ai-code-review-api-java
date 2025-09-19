package com.example.aicodereviewapi.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.aicodereviewapi.dto.review.ReviewRequest;
import com.example.aicodereviewapi.dto.review.ReviewResponse;
import com.example.aicodereviewapi.dto.review.ReviewWithAiRequest;
import com.example.aicodereviewapi.entity.Review;
import com.example.aicodereviewapi.repository.ReviewRepository;
import com.example.aicodereviewapi.security.JwtUserDetails;
import com.example.aicodereviewapi.service.openai.OpenaiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ReviewControllerTest {
    @Autowired private MockMvc mockMvc;

    @Autowired private ReviewRepository reviewRepository;

    @MockitoBean private OpenaiService openaiService;

    @Autowired private ObjectMapper objectMapper;

    private String userId;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        userId = UUID.randomUUID().toString();

        JwtUserDetails userDetails = new JwtUserDetails(userId, "testuser@example.com", "password");
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    void shouldCreateAndReturnReviewWhenCreateAndGetReview() throws Exception {
        ReviewRequest request = new ReviewRequest(UUID.randomUUID(), "Code", "Java", "gpt-4", "Review");

        String response =
                mockMvc
                        .perform(
                                post("/api/v1/reviews/")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(request.getCode()))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        ReviewResponse reviewResponse = objectMapper.readValue(response, ReviewResponse.class);

        mockMvc
                .perform(get("/api/v1/reviews/{id}", reviewResponse.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reviewResponse.getId().toString()));
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    void shouldReturnReviewsWhenGetReviews() throws Exception {
        for (int i = 0; i < 5; i++) {
            Review review = createRandomReview();
            reviewRepository.save(review);
        }

        mockMvc
                .perform(get("/api/v1/reviews/").param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviews", hasSize(5)));
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    void shouldDeleteReviewWhenDeleteReview() throws Exception {
        Review review = createRandomReview();
        reviewRepository.save(review);

        mockMvc.perform(delete("/api/v1/reviews/{id}", review.getId())).andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/reviews/{id}", review.getId())).andExpect(status().isNotFound());
    }

    Review createRandomReview() {
        return new Review(
                UUID.randomUUID().toString(), userId, "Code", "Review", "gpt-4", "Java", null);
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    void shouldCreateReviewWhenCreateReviewWithAi() throws Exception {
        String code = "Code", lang = "Java", review = "Review";
        when(openaiService.chat(code, lang)).thenReturn(review);

        ReviewWithAiRequest request = new ReviewWithAiRequest(UUID.randomUUID(), code, "gpt-4", lang);

        mockMvc
                .perform(
                        post("/api/v1/reviews/ai")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(review));
    }
}
