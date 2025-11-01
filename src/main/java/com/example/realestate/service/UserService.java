package com.example.realestate.service;

import com.example.realestate.dto.RegisterRequest;
import com.example.realestate.dto.UserUpdateRequest;
import com.example.realestate.dto.UserResponse;
import com.example.realestate.entity.User;

import java.util.List;

public interface UserService extends org.springframework.security.core.userdetails.UserDetailsService {
    User findByEmail(String email);
    UserResponse registerClient(RegisterRequest request);
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
    UserResponse toggleUserStatus(Long id);
    User updateUserProfile(User user); // AJOUTER cette méthode
}