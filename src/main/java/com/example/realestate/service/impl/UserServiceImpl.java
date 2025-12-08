package com.example.realestate.service.impl;

import com.example.realestate.dto.RegisterRequest;
import com.example.realestate.dto.UserUpdateRequest;
import com.example.realestate.dto.UserResponse;
import com.example.realestate.entity.User;
import com.example.realestate.entity.enums.Role;
import com.example.realestate.repository.UserRepository;
import com.example.realestate.service.EmailService;
import com.example.realestate.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // NOUVEAU : pour les logs
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j  // NOUVEAU : active les logs dans la classe
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    @Override
    public UserResponse registerClient(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = User.builder()
                .nom(request.getNom())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .telephone(request.getTelephone())
                .role(Role.ROLE_USER)  // Changé en CLIENT si c'est le rôle pour clients ; adaptez si ROLE_USER est correct
                .enabled(true)
                .build();
        user = userRepository.save(user);

        // ACTIVÉ : Envoi de l'email de bienvenue (comme pour createClientByAdmin)
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getNom());
            log.info("Email de bienvenue envoyé à l'utilisateur nouvellement enregistré : {}", user.getEmail());
        } catch (Exception e) {
            log.error("Échec envoi email de bienvenue à {} : {}", user.getEmail(), e.getMessage());
            // Optionnel : Ne bloque pas la création, mais log l'erreur
        }

        return mapToResponse(user);
    }

    // Nouvelle méthode pour création par admin (inchangée, mais avec log pour cohérence)
    @Override
    public UserResponse createClientByAdmin(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = User.builder()
                .nom(request.getNom())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .telephone(request.getTelephone())
                .role(Role.ROLE_USER)
                .enabled(true)
                .build();
        user = userRepository.save(user);

        // Envoi de l'email au nouveau client
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getNom());
            log.info("Email de bienvenue envoyé par admin à : {}", user.getEmail());
        } catch (Exception e) {
            log.error("Échec envoi email par admin à {} : {}", user.getEmail(), e.getMessage());
        }

        return mapToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        user.setNom(request.getNom());
        user.setTelephone(request.getTelephone());
        user = userRepository.save(user);
        return mapToResponse(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserResponse toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        user.setEnabled(!user.getEnabled());
        user = userRepository.save(user);
        return mapToResponse(user);
    }

    @Override
    public User updateUserProfile(User user) {
        return userRepository.save(user);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nom(user.getNom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}