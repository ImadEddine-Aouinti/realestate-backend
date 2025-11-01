package com.example.realestate.config;

import com.example.realestate.entity.User;
import com.example.realestate.entity.enums.Role;
import com.example.realestate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Vérifier si des utilisateurs existent déjà
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = User.builder()
                    .nom("Admin User")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .telephone("+1234567890")
                    .role(Role.ROLE_ADMIN)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            System.out.println("✓ Admin user created: admin@example.com / admin123");
        }

        if (userRepository.findByEmail("client@example.com").isEmpty()) {
            User client = User.builder()
                    .nom("Client User")
                    .email("client@example.com")
                    .password(passwordEncoder.encode("client123"))
                    .telephone("+0987654321")
                    .role(Role.ROLE_USER)
                    .enabled(true)
                    .build();
            userRepository.save(client);
            System.out.println("✓ Client user created: client@example.com / client123");
        }

        // Afficher le nombre d'utilisateurs créés
        long userCount = userRepository.count();
        System.out.println("✓ Total users in database: " + userCount);
    }
}