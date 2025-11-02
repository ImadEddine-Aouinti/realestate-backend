package com.example.realestate.config;

import com.example.realestate.entity.Property;
import com.example.realestate.entity.User;
import com.example.realestate.entity.Image;
import com.example.realestate.entity.enums.Role;
import com.example.realestate.repository.PropertyRepository;
import com.example.realestate.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PropertyRepository propertyRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        createSampleUsers();
        createSampleProperties();
    }

    private void createSampleUsers() {
        if (userRepository.count() == 0) {
            // Mot de passe par défaut : "password123" (encodé)
            String rawPassword = "123456";

            List<User> users = Arrays.asList(
                    User.builder()
                            .nom("Admin User")
                            .email("admin@example.com")
                            .telephone("+1234567890")
                            .password(passwordEncoder.encode(rawPassword))
                            .role(Role.ROLE_ADMIN)
                            .enabled(true)
                            .build(),

                    User.builder()
                            .nom("Client1 test")
                            .email("client1@example.com")
                            .telephone("+0987654321")
                            .password(passwordEncoder.encode(rawPassword))
                            .role(Role.ROLE_USER)
                            .enabled(true)
                            .build(),

                    User.builder()
                            .nom("Client2 test")
                            .email("client2@example.com")
                            .telephone("+0987654321")
                            .password(passwordEncoder.encode(rawPassword))
                            .role(Role.ROLE_USER)
                            .enabled(true)
                            .build(),
                    User.builder()
                            .nom("Client3 test")
                            .email("client3@example.com")
                            .telephone("+0987654321")
                            .password(passwordEncoder.encode(rawPassword))
                            .role(Role.ROLE_USER)
                            .enabled(true)
                            .build(),
                    User.builder()
                            .nom("Client4 test")
                            .email("client4@example.com")
                            .telephone("+0987654321")
                            .password(passwordEncoder.encode(rawPassword))
                            .role(Role.ROLE_USER)
                            .enabled(true)
                            .build(),
                    User.builder()
                            .nom("Client5 test")
                            .email("client5@example.com")
                            .telephone("+0987654321")
                            .password(passwordEncoder.encode(rawPassword))
                            .role(Role.ROLE_USER)
                            .enabled(true)
                            .build()
            );

            userRepository.saveAll(users);
            System.out.println("✅ " + users.size() + " utilisateurs de démonstration créés avec succès (mot de passe par défaut: 'password123')");
        }
    }

    private void createSampleProperties() {
        if (propertyRepository.count() == 0) {
            // Récupérer les utilisateurs existants
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                System.out.println("❌ Aucun utilisateur trouvé pour créer des propriétés");
                return;
            }

            User adminOwner = users.get(0); // Premier utilisateur : Admin
            User client1Owner = users.get(1); // Client1
            User client2Owner = users.get(2); // Client2 (CORRIGÉ : index 2)
            User client3Owner = users.get(3); // Client3 (CORRIGÉ : index 3)
            User client4Owner = users.get(4); // Client4 (CORRIGÉ : index 4)
            User client5Owner = users.get(5); // Client5 (CORRIGÉ : index 5)

            List<Property> properties = Arrays.asList(
                    createProperty(client1Owner, "Villa Moderne avec Piscine",
                            "Magnifique villa contemporaine avec piscine privative et jardin paysager. Située dans un quartier résidentiel calme.",
                            new BigDecimal("750000"), Property.PropertyType.VILLA, Property.PropertyStatus.AVAILABLE,
                            180.0, 4, 3, 6, 2018, "123 Avenue des Champs-Élysées", "Paris", "75008", "France",
                            true, true, true, true, false, true, true, "Cuisine équipée, dressing, cave à vin",
                            Arrays.asList(
                                    Image.builder().url("https://images.unsplash.com/photo-1580587771525-78b9dba3b914?w=800").isMain(true).altText("Vue extérieure de la villa moderne").build(),
                                    Image.builder().url("https://images.unsplash.com/photo-1570129477492-45c003edd2be?w=800").isMain(false).altText("Piscine privative et jardin paysager").build(),
                                    Image.builder().url("https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800").isMain(false).altText("Intérieur salon spacieux").build()
                            )),

                    createProperty(client2Owner, "Appartement Centre-Ville Lumineux",
                            "Bel appartement lumineux au cœur de la ville, proche de tous les commerces et transports.",
                            new BigDecimal("350000"), Property.PropertyType.APARTMENT, Property.PropertyStatus.AVAILABLE,
                            75.0, 2, 1, 3, 2015, "45 Rue de la République", "Lyon", "69001", "France",
                            false, false, false, true, true, true, true, "Double vitrage, parquet, cuisine aménagée",
                            Arrays.asList(
                                    Image.builder().url("https://images.unsplash.com/photo-1501183638710-841dd1904471?w=800").isMain(true).altText("Vue d'ensemble de l'appartement lumineux").build(),
                                    Image.builder().url("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800").isMain(false).altText("Chambre avec lumière naturelle").build(),
                                    Image.builder().url("https://images.unsplash.com/photo-1527030280862-64139fba04ca?w=800").isMain(false).altText("Cuisine aménagée moderne").build()
                            )),

                    createProperty(client3Owner, "Maison de Famille Spacieuse",
                            "Parfaite pour une famille, cette maison offre 4 chambres et un grand jardin arboré.",
                            new BigDecimal("520000"), Property.PropertyType.HOUSE, Property.PropertyStatus.PENDING,
                            140.0, 4, 2, 5, 2005, "78 Rue du Général Leclerc", "Marseille", "13001", "France",
                            true, true, false, false, false, false, true, "Garage, cave, buanderie",
                            Arrays.asList(
                                    Image.builder().url("https://images.unsplash.com/photo-1572120360610-d971b9d7767c?w=800").isMain(true).altText("Façade de la maison familiale spacieuse").build(),
                                    Image.builder().url("https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800").isMain(false).altText("Grand jardin arboré").build(),
                                    Image.builder().url("https://www.silencecapousse-chezvous.fr/media/images/15392/rectangle/w900/1587397108/BaseCreerJardin2.jpg").isMain(false).altText("Chambre pour enfants").build(),
                                    Image.builder().url("https://images.unsplash.com/photo-1600607687938-2a115d8e9f69?w=800").isMain(false).altText("Cuisine familiale fonctionnelle").build()
                            )),

                    createProperty(client4Owner, "Duplex avec Terrasse Panoramique",
                            "Superbe duplex avec grande terrasse offrant une vue panoramique sur la ville.",
                            new BigDecimal("420000"), Property.PropertyType.APARTMENT, Property.PropertyStatus.AVAILABLE,
                            95.0, 3, 2, 4, 2020, "22 Boulevard Saint-Germain", "Paris", "75005", "France",
                            true, false, false, true, true, true, true, "Terrasse 25m², vue panoramique, domotique",
                            Arrays.asList(
                                    Image.builder().url("https://images.unsplash.com/photo-1600585154526-990dced4db0d?w=800").isMain(true).altText("Terrasse panoramique avec vue sur la ville").build(),
                                    Image.builder().url("https://images.unsplash.com/photo-1600566752355-35792c689b4b?w=800").isMain(false).altText("Vue imprenable depuis la terrasse").build(),
                                    Image.builder().url("https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800").isMain(false).altText("Intérieur moderne du duplex").build()
                            )),

                    createProperty(client5Owner, "Studio Étudiant Proche Université",
                            "Studio fonctionnel idéal pour étudiant, proche des universités et commodités.",
                            new BigDecimal("180000"), Property.PropertyType.APARTMENT, Property.PropertyStatus.SOLD,
                            25.0, 1, 1, 1, 2010, "15 Rue de l'Université", "Toulouse", "31000", "France",
                            false, false, false, false, true, false, true, "Mezzanine, internet fibre inclus",
                            Arrays.asList(
                                    Image.builder().url("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800").isMain(true).altText("Studio compact et fonctionnel").build(),
                                    Image.builder().url("https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800").isMain(false).altText("Mezzanine pour espace supplémentaire").build()
                            ))
            );

            propertyRepository.saveAll(properties);
            System.out.println("✅ " + properties.size() + " propriétés de démonstration créées avec succès, chacune avec ses images uniques");
        }
    }

    private Property createProperty(User owner, String title, String description, BigDecimal price,
                                    Property.PropertyType type, Property.PropertyStatus status,
                                    double surface, int bedrooms, int bathrooms, int rooms, int yearBuilt,
                                    String address, String city, String postalCode, String country,
                                    boolean hasParking, boolean hasGarden, boolean hasPool, boolean hasBalcony,
                                    boolean hasElevator, boolean hasAirConditioning, boolean hasHeating,
                                    String additionalFeatures, List<Image> imagesList) {
        // Construire la propriété sans images d'abord
        Property property = Property.builder()
                .title(title)
                .description(description)
                .price(price)
                .type(type)
                .status(status)
                .owner(owner)
                .surface(surface)
                .bedrooms(bedrooms)
                .bathrooms(bathrooms)
                .rooms(rooms)
                .yearBuilt(yearBuilt)
                .address(address)
                .city(city)
                .postalCode(postalCode)
                .country(country)
                .hasParking(hasParking)
                .hasGarden(hasGarden)
                .hasPool(hasPool)
                .hasBalcony(hasBalcony)
                .hasElevator(hasElevator)
                .hasAirConditioning(hasAirConditioning)
                .hasHeating(hasHeating)
                .additionalFeatures(additionalFeatures)
                .build();

        // Associer la propriété à chaque image (côté bidirectionnel)
        for (Image image : imagesList) {
            image.setProperty(property);
        }

        // Associer les images à la propriété
        property.setImages(imagesList);

        return property;
    }
}