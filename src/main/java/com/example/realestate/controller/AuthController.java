package com.example.realestate.controller;

import com.example.realestate.dto.LoginRequest;
import com.example.realestate.dto.RegisterRequest;
import com.example.realestate.dto.JwtResponse;
import com.example.realestate.dto.UserResponse;
import com.example.realestate.service.AuthService;
import com.example.realestate.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.registerClient(request);
        return ResponseEntity.ok(response);
    }
}