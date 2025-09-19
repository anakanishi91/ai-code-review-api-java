package com.example.aicodereviewapi.dto.error;

import com.example.aicodereviewapi.exception.ErrorCode;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ApiErrorResponse {
    private ErrorCode errorCode;
    private String message;
}
