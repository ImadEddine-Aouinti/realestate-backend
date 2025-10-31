package com.example.realestate.service;

import com.example.realestate.entity.User;
import com.example.realestate.repository.UserRepository;
import com.example.realestate.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;

    public String register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        // ✅ Passe email et rôle à generateToken
        return jwtService.generateToken(user.getEmail(), String.valueOf(user.getRole()));
    }

    public String login(String email, String password) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        User user = userRepository.findByEmail(email).orElseThrow();
        // ✅ Passe email et rôle à generateToken
        return jwtService.generateToken(user.getEmail(), String.valueOf(user.getRole()));
    }
}
