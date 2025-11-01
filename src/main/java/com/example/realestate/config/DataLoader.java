package com.example.realestate.config;

import com.example.realestate.entity.Property;
import com.example.realestate.entity.User;
import com.example.realestate.entity.enums.Role;
import com.example.realestate.repository.PropertyRepository;
import com.example.realestate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createSampleUsers();
        createSampleProperties();
    }

    private void createSampleUsers() {
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

        System.out.println("✓ Total users in database: " + userRepository.count());
    }

    private void createSampleProperties() {
        User admin = userRepository.findByEmail("admin@example.com").orElse(null);

        if (admin != null && propertyRepository.count() == 0) {
            Property property1 = Property.builder()
                    .title("Belle maison avec jardin")
                    .description("Magnifique maison de 4 pièces avec grand jardin et garage")
                    .price(new BigDecimal("350000"))
                    .type(Property.PropertyType.HOUSE)
                    .status(Property.PropertyStatus.AVAILABLE)
                    .owner(admin)
                    .build();

            Property property2 = Property.builder()
                    .title("Appartement moderne centre-ville")
                    .description("Appartement neuf de 3 pièces au cœur de la ville")
                    .price(new BigDecimal("250000"))
                    .type(Property.PropertyType.APARTMENT)
                    .status(Property.PropertyStatus.AVAILABLE)
                    .owner(admin)
                    .build();

            propertyRepository.saveAll(List.of(property1, property2));
            System.out.println("✓ Sample properties created.");
        }
    }
}
