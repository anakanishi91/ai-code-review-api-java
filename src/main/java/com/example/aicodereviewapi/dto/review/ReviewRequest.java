package com.example.aicodereviewapi.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewRequest {
    @NotNull private UUID id;

    @NotBlank(message = "Code cannot be blank")
    private String code;

    @NotBlank(message = "Chat model ID cannot be blank")
    private String chatModelId;

    @NotBlank(message = "Programming language cannot be blank")
    private String programmingLanguage;

    @NotBlank(message = "Review cannot be blank")
    private String review;
}
