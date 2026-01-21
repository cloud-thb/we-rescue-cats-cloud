package io.werescuecats.backend.service;

import io.werescuecats.backend.entity.*;
import io.werescuecats.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CatRepository catRepository;

    @Mock
    private AdoptionRepository adoptionRepository;

    @Mock
    private BreedRepository breedRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataInitializationService service;

    @BeforeEach
    void setUp() {
        lenient().when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
    }

    @Test
    void initializeData_shouldSkipWhenBreedsNotLoaded() {
        // Given
        when(breedRepository.count()).thenReturn(0L);

        // When
        service.initializeData();

        // Then
        verify(userRepository, never()).save(any());
        verify(catRepository, never()).save(any());
        verify(adoptionRepository, never()).save(any());
    }

    @Test
    void initializeData_shouldCreateAllDataWhenDatabaseIsEmpty() {
        // Given
        when(breedRepository.count()).thenReturn(5L);
        when(userRepository.count()).thenReturn(0L);
        when(catRepository.count()).thenReturn(0L);
        when(adoptionRepository.count()).thenReturn(0L);
        when(breedRepository.findAll()).thenReturn(createMockBreeds());
        when(userRepository.findByRole(UserRole.USER)).thenReturn(createMockUsers());
        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(createMockAdmins());
        when(catRepository.findByStatus(CatStatus.AVAILABLE)).thenReturn(createMockCats());

        // When
        service.initializeData();

        // Then
        verify(userRepository, times(6)).save(any(User.class)); // 1 admin + 5 users
        verify(catRepository, atLeast(24)).save(any(Cat.class)); // 24 cats + some updated during adoptions
        verify(adoptionRepository, atLeast(1)).save(any(Adoption.class));
    }

    @Test
    void createSampleUsers_shouldCreateAdminWithCorrectProperties() {
        // Given
        when(breedRepository.count()).thenReturn(5L);
        when(userRepository.count()).thenReturn(0L);
        when(catRepository.count()).thenReturn(1L); // Skip cat creation
        when(adoptionRepository.count()).thenReturn(1L); // Skip adoption creation

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // When
        service.initializeData();

        // Then
        verify(userRepository, times(6)).save(userCaptor.capture());

        User admin = userCaptor.getAllValues().get(0);
        assertEquals("admin@werescuecats.io", admin.getEmail());
        assertEquals("Admin", admin.getFirstName());
        assertEquals("User", admin.getLastName());
        assertEquals(UserRole.ADMIN, admin.getRole());
        assertEquals("main", admin.getTenantId());
        assertEquals("encodedPassword", admin.getPasswordHash());
        assertNotNull(admin.getStreetAddress());
        assertNotNull(admin.getPostalCode());
    }

    @Test
    void createSampleUsers_shouldCreateRegularUsersWithCorrectProperties() {
        // Given
        when(breedRepository.count()).thenReturn(5L);
        when(userRepository.count()).thenReturn(0L);
        when(catRepository.count()).thenReturn(1L); // Skip cat creation
        when(adoptionRepository.count()).thenReturn(1L); // Skip adoption creation

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // When
        service.initializeData();

        // Then
        verify(userRepository, times(6)).save(userCaptor.capture());

        // Check regular users (skip admin at index 0)
        List<User> regularUsers = userCaptor.getAllValues().subList(1, 6);

        for (User user : regularUsers) {
            assertEquals(UserRole.USER, user.getRole());
            assertEquals("main", user.getTenantId());
            assertEquals("encodedPassword", user.getPasswordHash());
            assertNotNull(user.getEmail());
            assertNotNull(user.getFirstName());
            assertNotNull(user.getLastName());
            assertNotNull(user.getStreetAddress());
            assertNotNull(user.getPostalCode());
        }

        // Verify specific user
        User johnDoe = regularUsers.get(0);
        assertEquals("john.doe@example.com", johnDoe.getEmail());
        assertEquals("John", johnDoe.getFirstName());
        assertEquals("Doe", johnDoe.getLastName());
    }

    @Test
    void createSampleCats_shouldCreateCatsWithValidProperties() {
        // Given
        when(breedRepository.count()).thenReturn(5L);
        when(userRepository.count()).thenReturn(1L); // Skip user creation
        when(catRepository.count()).thenReturn(0L);
        when(adoptionRepository.count()).thenReturn(1L); // Skip adoption creation
        when(breedRepository.findAll()).thenReturn(createMockBreeds());

        ArgumentCaptor<Cat> catCaptor = ArgumentCaptor.forClass(Cat.class);

        // When
        service.initializeData();

        // Then
        verify(catRepository, times(24)).save(catCaptor.capture());

        for (Cat cat : catCaptor.getAllValues()) {
            assertNotNull(cat.getName());
            assertTrue(cat.getAge() >= 1 && cat.getAge() <= 8);
            assertNotNull(cat.getDescription());
            assertNotNull(cat.getBreed());
            assertNotNull(cat.getImageUrl());
            assertNotNull(cat.getGender());
            assertTrue(cat.getGender().equals("MALE") || cat.getGender().equals("FEMALE"));
            assertNotNull(cat.getLatitude());
            assertNotNull(cat.getLongitude());
            assertNotNull(cat.getAddress());
            assertNotNull(cat.getStatus());
            assertTrue(cat.getStatus() == CatStatus.AVAILABLE || cat.getStatus() == CatStatus.ADOPTED);

            // Check coordinates are around Dresden
            assertTrue(cat.getLatitude() > 50.5 && cat.getLatitude() < 51.5);
            assertTrue(cat.getLongitude() > 13.0 && cat.getLongitude() < 14.5);
        }
    }

    @Test
    void createSampleAdoptions_shouldCreateAdoptionsWithValidStatuses() {
        // Given
        when(breedRepository.count()).thenReturn(5L);
        when(userRepository.count()).thenReturn(1L); // Skip user creation
        when(catRepository.count()).thenReturn(1L); // Skip cat creation
        when(adoptionRepository.count()).thenReturn(0L);
        when(userRepository.findByRole(UserRole.USER)).thenReturn(createMockUsers());
        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(createMockAdmins());
        when(catRepository.findByStatus(CatStatus.AVAILABLE)).thenReturn(createMockCats());

        ArgumentCaptor<Adoption> adoptionCaptor = ArgumentCaptor.forClass(Adoption.class);

        // When
        service.initializeData();

        // Then
        verify(adoptionRepository, atLeast(1)).save(adoptionCaptor.capture());

        for (Adoption adoption : adoptionCaptor.getAllValues()) {
            assertNotNull(adoption.getUser());
            assertNotNull(adoption.getCat());
            assertNotNull(adoption.getNotes());
            assertNotNull(adoption.getStatus());
            assertNotNull(adoption.getAdoptionDate());

            // Verify status-specific fields
            if (adoption.getStatus() == AdoptionStatus.APPROVED ||
                    adoption.getStatus() == AdoptionStatus.COMPLETED) {
                assertNotNull(adoption.getApprovedDate());
                assertNotNull(adoption.getProcessedByAdmin());
            }

            if (adoption.getStatus() == AdoptionStatus.COMPLETED) {
                assertNotNull(adoption.getCompletedDate());
            }
        }
    }

    @Test
    void createSampleCats_shouldSkipWhenNoBreedsAvailable() {
        // Given
        when(breedRepository.count()).thenReturn(5L);
        when(userRepository.count()).thenReturn(1L); // Skip user creation
        when(catRepository.count()).thenReturn(0L);
        when(adoptionRepository.count()).thenReturn(1L); // Skip adoption creation
        when(breedRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        service.initializeData();

        // Then
        verify(catRepository, never()).save(any(Cat.class));
    }

    @Test
    void createSampleAdoptions_shouldSkipWhenInsufficientData() {
        // Given
        when(breedRepository.count()).thenReturn(5L);
        when(userRepository.count()).thenReturn(1L); // Skip user creation
        when(catRepository.count()).thenReturn(1L); // Skip cat creation
        when(adoptionRepository.count()).thenReturn(0L);
        when(userRepository.findByRole(UserRole.USER)).thenReturn(Collections.emptyList());
        when(catRepository.findByStatus(CatStatus.AVAILABLE)).thenReturn(createMockCats());

        // When
        service.initializeData();

        // Then
        verify(adoptionRepository, never()).save(any(Adoption.class));
    }

    // Helper methods to create mock data
    private List<Breed> createMockBreeds() {
        Breed breed1 = new Breed();
        breed1.setId("abys");
        breed1.setName("Abyssinian");
        breed1.setImageUrl("https://example.com/abys.jpg");

        Breed breed2 = new Breed();
        breed2.setId("beng");
        breed2.setName("Bengal");
        breed2.setImageUrl("https://example.com/beng.jpg");

        return Arrays.asList(breed1, breed2);
    }

    private List<User> createMockUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");
        user1.setRole(UserRole.USER);

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        user2.setRole(UserRole.USER);

        return Arrays.asList(user1, user2);
    }

    private List<User> createMockAdmins() {
        User admin = new User();
        admin.setId(999L);
        admin.setEmail("admin@example.com");
        admin.setRole(UserRole.ADMIN);

        return Arrays.asList(admin);
    }

    private List<Cat> createMockCats() {
        Cat cat1 = new Cat();
        cat1.setId(1L);
        cat1.setName("Whiskers");
        cat1.setStatus(CatStatus.AVAILABLE);
        cat1.setBreed(createMockBreeds().get(0));

        Cat cat2 = new Cat();
        cat2.setId(2L);
        cat2.setName("Luna");
        cat2.setStatus(CatStatus.AVAILABLE);
        cat2.setBreed(createMockBreeds().get(1));

        Cat cat3 = new Cat();
        cat3.setId(3L);
        cat3.setName("Oliver");
        cat3.setStatus(CatStatus.AVAILABLE);
        cat3.setBreed(createMockBreeds().get(0));

        Cat cat4 = new Cat();
        cat4.setId(4L);
        cat4.setName("Max");
        cat4.setStatus(CatStatus.AVAILABLE);
        cat4.setBreed(createMockBreeds().get(1));

        Cat cat5 = new Cat();
        cat5.setId(5L);
        cat5.setName("Bella");
        cat5.setStatus(CatStatus.AVAILABLE);
        cat5.setBreed(createMockBreeds().get(0));

        Cat cat6 = new Cat();
        cat6.setId(6L);
        cat6.setName("Charlie");
        cat6.setStatus(CatStatus.AVAILABLE);
        cat6.setBreed(createMockBreeds().get(1));

        Cat cat7 = new Cat();
        cat7.setId(7L);
        cat7.setName("Lucy");
        cat7.setStatus(CatStatus.AVAILABLE);
        cat7.setBreed(createMockBreeds().get(0));

        Cat cat8 = new Cat();
        cat8.setId(8L);
        cat8.setName("Leo");
        cat8.setStatus(CatStatus.AVAILABLE);
        cat8.setBreed(createMockBreeds().get(1));

        return Arrays.asList(cat1, cat2, cat3, cat4, cat5, cat6, cat7, cat8);
    }
}
