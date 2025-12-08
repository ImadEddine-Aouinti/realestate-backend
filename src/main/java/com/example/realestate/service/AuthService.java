package com.example.realestate.service;

import com.example.realestate.dto.LoginRequest;
import com.example.realestate.dto.JwtResponse;

public interface AuthService {
    JwtResponse login(LoginRequest request);
}