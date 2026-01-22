package io.werescuecats.backend.service;

import io.werescuecats.backend.entity.*;
import io.werescuecats.backend.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class DataInitializationService {

    private final UserRepository userRepository;

    private final CatRepository catRepository;

    private final AdoptionRepository adoptionRepository;

    private final BreedRepository breedRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * SecureRandom is used to satisfy SonarCloud security rules,
     * even though this randomness is only used for demo data.
     */
    private final SecureRandom random = new SecureRandom();

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeData() {
        log.info("Initializing sample data...");

        // Wait for breeds to be loaded first
        if (breedRepository.count() == 0) {
            log.info("Breeds not yet loaded, skipping data initialization");
            return;
        }

        if (userRepository.count() == 0) {
            createSampleUsers();
        }

        if (catRepository.count() == 0) {
            createSampleCats();
        }

        if (adoptionRepository.count() == 0) {
            createSampleAdoptions();
        }

        log.info("Sample data initialization completed!");
    }

    private void createSampleUsers() {
        log.info("Creating sample users...");

        // Admin user
        User admin = new User();
        admin.setEmail("admin@werescuecats.io");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setStreetAddress("123 Admin Street");
        admin.setPostalCode("12345");
        admin.setRole(UserRole.ADMIN);
        admin.setTenantId("main");
        userRepository.save(admin);

        log.info("Encoded admin password: {}", admin.getPasswordHash());

        // Regular users
        List<String[]> userList = Arrays.asList(
                new String[] { "john.doe@example.com", "John", "Doe", "456 Oak Avenue", "54321" },
                new String[] { "jane.smith@example.com", "Jane", "Smith", "789 Pine Street", "67890" },
                new String[] { "mike.johnson@example.com", "Mike", "Johnson", "321 Elm Road", "13579" },
                new String[] { "sarah.wilson@example.com", "Sarah", "Wilson", "654 Maple Drive", "24680" },
                new String[] { "chris.brown@example.com", "Chris", "Brown", "987 Cedar Lane", "97531" });

        for (String[] userData : userList) {
            User user = new User();
            user.setEmail(userData[0]);
            user.setPasswordHash(passwordEncoder.encode("password123"));
            user.setFirstName(userData[1]);
            user.setLastName(userData[2]);
            user.setStreetAddress(userData[3]);
            user.setPostalCode(userData[4]);
            user.setRole(UserRole.USER);
            user.setTenantId("main");
            userRepository.save(user);
        }

        log.info("Created {} users", userRepository.count());
    }

    private void createSampleCats() {
        log.info("Creating sample cats...");

        List<Breed> availableBreeds = breedRepository.findAll();
        if (availableBreeds.isEmpty()) {
            log.warn("No breeds available, cannot create cats");
            return;
        }


        // Dresden, Germany area coordinates
        double baseLat = 51.0504;
        double baseLon = 13.7373;

        String[] catNames = {
                "Whiskers", "Shadow", "Luna", "Oliver", "Mia", "Leo", "Bella", "Charlie",
                "Lucy", "Max", "Lily", "Jack", "Sophie", "Tiger", "Coco", "Simba",
                "Nala", "Felix", "Molly", "Oscar", "Ruby", "Jasper", "Chloe", "Smokey"
        };

        String[] descriptions = {
                "A friendly and playful cat looking for a loving home",
                "Very affectionate and loves to cuddle",
                "Independent but loyal, perfect for a quiet household",
                "Energetic and loves to play with toys",
                "Calm and gentle, great with children",
                "Curious and intelligent, needs mental stimulation",
                "Social butterfly who loves meeting new people",
                "Peaceful lap cat who enjoys quiet afternoons"
        };

        String[] addresses = {
                "Neustadt, Dresden", "Altstadt, Dresden", "Blasewitz, Dresden",
                "Pieschen, Dresden", "Cotta, Dresden", "Prohlis, Dresden",
                "Klotzsche, Dresden", "Loschwitz, Dresden", "Striesen, Dresden",
                "Gorbitz, Dresden", "Johannstadt, Dresden", "Reick, Dresden"
        };

        String[] gender = { "MALE", "FEMALE" };

        for (int i = 0; i < catNames.length; i++) {
            Cat cat = new Cat();
            cat.setName(catNames[i]);
            cat.setAge(random.nextInt(8) + 1); // 1-8 years old
            cat.setDescription(descriptions[random.nextInt(descriptions.length)]);
            Breed breed = availableBreeds.get(random.nextInt(availableBreeds.size()));
            cat.setBreed(breed);
            cat.setImageUrl(breed.getImageUrl());

            // Random coordinates around Dresden
            cat.setGender(gender[random.nextInt(gender.length)]);
            cat.setLatitude(baseLat + (random.nextGaussian() * 0.1));
            cat.setLongitude(baseLon + (random.nextGaussian() * 0.1));
            cat.setAddress(addresses[random.nextInt(addresses.length)]);

            // Most cats available, some adopted
            cat.setStatus(random.nextInt(10) < 8 ? CatStatus.AVAILABLE : CatStatus.ADOPTED);

            catRepository.save(cat);
        }

        log.info("Created {} cats", catRepository.count());
    }

    private void createSampleAdoptions() {
        log.info("Creating sample adoptions...");

        List<User> users = userRepository.findByRole(UserRole.USER);
        List<Cat> availableCats = catRepository.findByStatus(CatStatus.AVAILABLE);
        Optional<User> adminOpt = userRepository.findByRole(UserRole.ADMIN).stream().findFirst();

        if (users.isEmpty() || availableCats.isEmpty() || adminOpt.isEmpty()) {
            log.warn("Not enough data to create adoptions");
            return;
        }

        User admin = adminOpt.get();

        String[] adoptionNotes = {
                "I have experience with cats and would love to provide a loving home.",
                "My family is excited to welcome a new furry member.",
                "I live in a quiet apartment perfect for a cat.",
                "Looking for a companion cat to keep me company.",
                "I have a large house with plenty of space for a cat to explore."
        };

        // Create some adoptions in different states
        for (int i = 0; i < Math.min(8, users.size()); i++) {
            if (i >= availableCats.size())
                break;

            User user = users.get(i);
            Cat cat = availableCats.get(i);

            Adoption adoption = new Adoption(
                    user,
                    cat,
                    adoptionNotes[random.nextInt(adoptionNotes.length)]);

            // Set different adoption states
            if (i < 3) {
                // Pending adoptions
                adoption.setStatus(AdoptionStatus.PENDING);
            } else if (i < 6) {
                // Approved adoptions
                adoption.setStatus(AdoptionStatus.APPROVED);
                adoption.setApprovedDate(adoption.getAdoptionDate().plusDays(1));
                adoption.setProcessedByAdmin(admin);
                cat.setStatus(CatStatus.PENDING);
            } else {
                // Completed adoptions
                adoption.setStatus(AdoptionStatus.COMPLETED);
                adoption.setApprovedDate(adoption.getAdoptionDate().plusDays(1));
                adoption.setCompletedDate(adoption.getAdoptionDate().plusDays(3));
                adoption.setProcessedByAdmin(admin);
                cat.setStatus(CatStatus.ADOPTED);
            }

            adoptionRepository.save(adoption);
            catRepository.save(cat);
        }

        log.info("Created {} adoptions", adoptionRepository.count());
    }
}
