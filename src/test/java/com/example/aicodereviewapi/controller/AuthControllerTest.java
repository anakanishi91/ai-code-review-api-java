package com.example.aicodereviewapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.aicodereviewapi.dto.auth.LoginRequest;
import com.example.aicodereviewapi.dto.auth.RegisterRequest;
import com.example.aicodereviewapi.entity.User;
import com.example.aicodereviewapi.repository.UserRepository;
import com.example.aicodereviewapi.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserRepository userRepository;

    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateUserWhenSignup() throws Exception {
        String email = "test@example.com";
        String password = "password123";

        RegisterRequest request = new RegisterRequest(email, password);

        mockMvc
                .perform(
                        post("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.id").exists());

        User savedUser = userRepository.findByEmail(email).orElseThrow();
        assertTrue(passwordEncoder.matches(password, savedUser.getPassword()));
    }

    @Test
    void shouldReturnValidTokenWhenLogin() throws Exception {
        String userId = UUID.randomUUID().toString();
        String email = "test@example.com";
        String password = "password123";
        User user = new User(userId, email, passwordEncoder.encode(password), false);
        userRepository.save(user);

        LoginRequest request = new LoginRequest(email, password);

        String response =
                mockMvc
                        .perform(
                                post("/api/v1/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(userId))
                        .andExpect(jsonPath("$.email").value(email))
                        .andExpect(jsonPath("$.accessToken").exists())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        String token = objectMapper.readTree(response).get("accessToken").asText();
        String userIdFromToken = jwtUtil.extractUserId(token);
        assertEquals(user.getId(), userIdFromToken);
    }

    @Test
    void shouldFailWhenLoginWithInvalidPassword() throws Exception {
        String userId = UUID.randomUUID().toString();
        String email = "test@example.com";
        String password = "password123";
        User user = new User(userId, email, passwordEncoder.encode(password), false);
        userRepository.save(user);

        LoginRequest request = new LoginRequest(email, "wrongpassword");

        mockMvc
                .perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateGuestUserAndReturnJwtWhenGuestLogin() throws Exception {
        String response =
                mockMvc
                        .perform(post("/api/v1/auth/guest"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.accessToken").exists())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        String userId = objectMapper.readTree(response).get("id").asText();
        assertTrue(userRepository.findById(userId).isPresent());

        String token = objectMapper.readTree(response).get("accessToken").asText();
        String userIdFromToken = jwtUtil.extractUserId(token);
        assertEquals(userId, userIdFromToken);
    }
}
