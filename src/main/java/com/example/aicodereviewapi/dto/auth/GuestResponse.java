package com.example.aicodereviewapi.dto.auth;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GuestResponse {
    private UUID id;
    private String accessToken;
}
