package com.example.aicodereviewapi.controller;

import com.example.aicodereviewapi.dto.auth.*;
import com.example.aicodereviewapi.entity.User;
import com.example.aicodereviewapi.security.JwtUtil;
import com.example.aicodereviewapi.service.user.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.getEmail(), request.getPassword());
        return new RegisterResponse(UUID.fromString(user.getId()), user.getEmail());
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userService.findByEmail(request.getEmail());
        String token = jwtUtil.generateToken(user.getId());
        return new LoginResponse(UUID.fromString(user.getId()), user.getEmail(), token);
    }

    @PostMapping("/guest")
    public GuestResponse guest() {
        User user = userService.createGuestUser();
        String token = jwtUtil.generateToken(user.getId());
        return new GuestResponse(UUID.fromString(user.getId()), token);
    }
}
