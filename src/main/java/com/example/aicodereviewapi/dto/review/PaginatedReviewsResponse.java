package com.example.aicodereviewapi.dto.review;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PaginatedReviewsResponse {
    private List<ReviewResponse> reviews;
    private boolean hasMore;
}
