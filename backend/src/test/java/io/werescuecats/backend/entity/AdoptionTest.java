package io.werescuecats.backend.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AdoptionTest {

    private User testUser;
    private Cat testCat;

    @BeforeEach
    void setUp() {
        Breed testBreed = new Breed();
        testBreed.setName("Persian");

        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        testCat = new Cat();
        testCat.setName("Whiskers");
        testCat.setBreed(testBreed);
    }

    @Test
    @DisplayName("Should create adoption with default constructor")
    void testDefaultConstructor() {
        Adoption adoption = new Adoption();
        assertNotNull(adoption);
        assertNull(adoption.getId());
        assertNull(adoption.getUser());
        assertNull(adoption.getCat());
        assertNull(adoption.getStatus());
    }

    @Test
    @DisplayName("Should create adoption with user and cat")
    void testConstructorWithUserAndCat() {
        Adoption adoption = new Adoption(testUser, testCat);

        assertNotNull(adoption);
        assertEquals(testUser, adoption.getUser());
        assertEquals(testCat, adoption.getCat());
        assertEquals(AdoptionStatus.PENDING, adoption.getStatus());
        assertNotNull(adoption.getAdoptionDate());
    }

    @Test
    @DisplayName("Should create adoption with user, cat, and notes")
    void testConstructorWithUserCatAndNotes() {
        String notes = "Looking forward to adopting this cat!";
        Adoption adoption = new Adoption(testUser, testCat, notes);

        assertNotNull(adoption);
        assertEquals(testUser, adoption.getUser());
        assertEquals(testCat, adoption.getCat());
        assertEquals(notes, adoption.getNotes());
        assertEquals(AdoptionStatus.PENDING, adoption.getStatus());
        assertNotNull(adoption.getAdoptionDate());
    }

    @Test
    @DisplayName("Should set and get all properties correctly")
    void testGettersAndSetters() {
        Adoption adoption = new Adoption();
        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("User");

        LocalDateTime now = LocalDateTime.now();

        adoption.setId(1L);
        adoption.setUser(testUser);
        adoption.setCat(testCat);
        adoption.setStatus(AdoptionStatus.APPROVED);
        adoption.setAdoptionDate(now);
        adoption.setApprovedDate(now);
        adoption.setCompletedDate(now);
        adoption.setNotes("User notes");
        adoption.setAdminNotes("Admin notes");
        adoption.setProcessedByAdmin(admin);
        adoption.setTenantId("tenant123");
        adoption.setCreatedAt(now);
        adoption.setUpdatedAt(now);

        assertEquals(1L, adoption.getId());
        assertEquals(testUser, adoption.getUser());
        assertEquals(testCat, adoption.getCat());
        assertEquals(AdoptionStatus.APPROVED, adoption.getStatus());
        assertEquals(now, adoption.getAdoptionDate());
        assertEquals(now, adoption.getApprovedDate());
        assertEquals(now, adoption.getCompletedDate());
        assertEquals("User notes", adoption.getNotes());
        assertEquals("Admin notes", adoption.getAdminNotes());
        assertEquals(admin, adoption.getProcessedByAdmin());
        assertEquals("tenant123", adoption.getTenantId());
        assertEquals(now, adoption.getCreatedAt());
        assertEquals(now, adoption.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return true for isPending when status is PENDING")
    void testIsPending() {
        Adoption adoption = new Adoption(testUser, testCat);
        adoption.setStatus(AdoptionStatus.PENDING);
        assertTrue(adoption.isPending());
        assertFalse(adoption.isApproved());
        assertFalse(adoption.isCompleted());
        assertFalse(adoption.isRejected());
        assertFalse(adoption.isCancelled());
    }

    @Test
    @DisplayName("Should return true for isApproved when status is APPROVED")
    void testIsApproved() {
        Adoption adoption = new Adoption(testUser, testCat);
        adoption.setStatus(AdoptionStatus.APPROVED);
        assertTrue(adoption.isApproved());
        assertFalse(adoption.isPending());
        assertFalse(adoption.isCompleted());
        assertFalse(adoption.isRejected());
        assertFalse(adoption.isCancelled());
    }

    @Test
    @DisplayName("Should return true for isCompleted when status is COMPLETED")
    void testIsCompleted() {
        Adoption adoption = new Adoption(testUser, testCat);
        adoption.setStatus(AdoptionStatus.COMPLETED);
        assertTrue(adoption.isCompleted());
        assertFalse(adoption.isPending());
        assertFalse(adoption.isApproved());
        assertFalse(adoption.isRejected());
        assertFalse(adoption.isCancelled());
    }

    @Test
    @DisplayName("Should return true for isRejected when status is REJECTED")
    void testIsRejected() {
        Adoption adoption = new Adoption(testUser, testCat);
        adoption.setStatus(AdoptionStatus.REJECTED);
        assertTrue(adoption.isRejected());
        assertFalse(adoption.isPending());
        assertFalse(adoption.isApproved());
        assertFalse(adoption.isCompleted());
        assertFalse(adoption.isCancelled());
    }

    @Test
    @DisplayName("Should return true for isCancelled when status is CANCELLED")
    void testIsCancelled() {
        Adoption adoption = new Adoption(testUser, testCat);
        adoption.setStatus(AdoptionStatus.CANCELLED);
        assertTrue(adoption.isCancelled());
        assertFalse(adoption.isPending());
        assertFalse(adoption.isApproved());
        assertFalse(adoption.isCompleted());
        assertFalse(adoption.isRejected());
    }

    @Test
    @DisplayName("Should return true for isActive when status is PENDING")
    void testIsActiveWithPendingStatus() {
        Adoption adoption = new Adoption(testUser, testCat);
        adoption.setStatus(AdoptionStatus.PENDING);
        assertTrue(adoption.isActive());
    }

    @Test
    @DisplayName("Should return true for isActive when status is APPROVED")
    void testIsActiveWithApprovedStatus() {
        Adoption adoption = new Adoption(testUser, testCat);
        adoption.setStatus(AdoptionStatus.APPROVED);
        assertTrue(adoption.isActive());
    }

    @Test
    @DisplayName("Should return false for isActive when status is COMPLETED")
    void testIsNotActiveWithCompletedStatus() {
        Adoption adoption = new Adoption(testUser, testCat);
        adoption.setStatus(AdoptionStatus.COMPLETED);
        assertFalse(adoption.isActive());
    }

    @Test
    @DisplayName("Should return false for isActive when status is REJECTED")
    void testIsNotActiveWithRejectedStatus() {
        Adoption adoption = new Adoption(testUser, testCat);
        adoption.setStatus(AdoptionStatus.REJECTED);
        assertFalse(adoption.isActive());
    }

    @Test
    @DisplayName("Should return false for isActive when status is CANCELLED")
    void testIsNotActiveWithCancelledStatus() {
        Adoption adoption = new Adoption(testUser, testCat);
        adoption.setStatus(AdoptionStatus.CANCELLED);
        assertFalse(adoption.isActive());
    }

    @Test
    @DisplayName("Should return adopter name")
    void testGetAdopterName() {
        Adoption adoption = new Adoption(testUser, testCat);
        assertEquals("John Doe", adoption.getAdopterName());
    }

    @Test
    @DisplayName("Should return Unknown when user is null")
    void testGetAdopterNameWhenUserIsNull() {
        Adoption adoption = new Adoption();
        assertEquals("Unknown", adoption.getAdopterName());
    }

    @Test
    @DisplayName("Should return cat name")
    void testGetCatName() {
        Adoption adoption = new Adoption(testUser, testCat);
        assertEquals("Whiskers", adoption.getCatName());
    }

    @Test
    @DisplayName("Should return Unknown when cat is null")
    void testGetCatNameWhenCatIsNull() {
        Adoption adoption = new Adoption();
        assertEquals("Unknown", adoption.getCatName());
    }

    @Test
    @DisplayName("Should return cat breed")
    void testGetCatBreed() {
        Adoption adoption = new Adoption(testUser, testCat);
        assertEquals("Persian", adoption.getCatBreed());
    }

    @Test
    @DisplayName("Should return Unknown when cat is null")
    void testGetCatBreedWhenCatIsNull() {
        Adoption adoption = new Adoption();
        assertEquals("Unknown", adoption.getCatBreed());
    }

    @Test
    @DisplayName("Should return Unknown when breed is null")
    void testGetCatBreedWhenBreedIsNull() {
        testCat.setBreed(null);
        Adoption adoption = new Adoption(testUser, testCat);
        assertEquals("Unknown", adoption.getCatBreed());
    }

    @Test
    @DisplayName("Should calculate days from application correctly")
    void testGetDaysFromApplication() {
        Adoption adoption = new Adoption(testUser, testCat);
        adoption.setAdoptionDate(LocalDateTime.now().minusDays(5));
        assertEquals(5, adoption.getDaysFromApplication());
    }

    @Test
    @DisplayName("Should return 0 days when adoption date is today")
    void testGetDaysFromApplicationWhenToday() {
        Adoption adoption = new Adoption(testUser, testCat);
        adoption.setAdoptionDate(LocalDateTime.now());
        assertEquals(0, adoption.getDaysFromApplication());
    }

    @Test
    @DisplayName("Should set timestamps on onCreate")
    void testOnCreate() {
        Adoption adoption = new Adoption();
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        adoption.onCreate();

        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertNotNull(adoption.getCreatedAt());
        assertNotNull(adoption.getUpdatedAt());
        assertNotNull(adoption.getAdoptionDate());
        assertTrue(adoption.getCreatedAt().isAfter(before) && adoption.getCreatedAt().isBefore(after));
        assertTrue(adoption.getUpdatedAt().isAfter(before) && adoption.getUpdatedAt().isBefore(after));
    }

    @Test
    @DisplayName("Should not override existing adoption date on onCreate")
    void testOnCreateWithExistingAdoptionDate() {
        Adoption adoption = new Adoption();
        LocalDateTime existingDate = LocalDateTime.now().minusDays(10);
        adoption.setAdoptionDate(existingDate);

        adoption.onCreate();

        assertEquals(existingDate, adoption.getAdoptionDate());
    }

    @Test
    @DisplayName("Should update timestamp on onUpdate")
    void testOnUpdate() {
        Adoption adoption = new Adoption();
        adoption.onCreate();
        LocalDateTime originalUpdatedAt = adoption.getUpdatedAt();

        try {
            Thread.sleep(10); // Small delay to ensure different timestamp
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        adoption.onUpdate();

        assertNotNull(adoption.getUpdatedAt());
        assertTrue(adoption.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    @DisplayName("Should generate correct toString")
    void testToString() {
        Adoption adoption = new Adoption(testUser, testCat);
        adoption.setId(1L);
        adoption.setStatus(AdoptionStatus.PENDING);

        String result = adoption.toString();

        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("user=John Doe"));
        assertTrue(result.contains("cat=Whiskers"));
        assertTrue(result.contains("status=PENDING"));
        assertTrue(result.contains("adoptionDate="));
    }

    @Test
    @DisplayName("Should handle null user in toString")
    void testToStringWithNullUser() {
        Adoption adoption = new Adoption();
        adoption.setCat(testCat);

        String result = adoption.toString();

        assertTrue(result.contains("user=null"));
    }

    @Test
    @DisplayName("Should handle null cat in toString")
    void testToStringWithNullCat() {
        Adoption adoption = new Adoption();
        adoption.setUser(testUser);

        String result = adoption.toString();

        assertTrue(result.contains("cat=null"));
    }
}