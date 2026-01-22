package io.werescuecats.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "adoptions")
public class Adoption {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    @Column(name = "adoption_id")
    private Long id;

    @Getter @Setter
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Getter @Setter
    @NotNull(message = "Cat is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id", nullable = false)
    private Cat cat;
    
    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AdoptionStatus status;
    
    @Getter @Setter
    @Column(name = "adoption_date", nullable = false)
    private LocalDateTime adoptionDate;
    
    @Getter @Setter
    @Column(name = "approved_date")
    private LocalDateTime approvedDate;
    
    @Getter @Setter
    @Column(name = "completed_date")
    private LocalDateTime completedDate;
    
    @Getter @Setter
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Getter @Setter
    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;
    
    @Getter @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_admin_id")
    private User processedByAdmin;
    
    @Getter @Setter
    @Column(name = "tenant_id", length = 50)
    private String tenantId;

    @Getter @Setter
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Getter @Setter
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private static final String UNKNOWN = "Unknown";
    
    public Adoption() {}
    
    public Adoption(User user, Cat cat) {
        this.user = user;
        this.cat = cat;
        this.adoptionDate = LocalDateTime.now();
        this.status = AdoptionStatus.PENDING;
    }
    
    public Adoption(User user, Cat cat, String notes) {
        this.user = user;
        this.cat = cat;
        this.adoptionDate = LocalDateTime.now();
        this.status = AdoptionStatus.PENDING;
        this.notes = notes;
    }

    public Adoption(Long catId) {
        this.user = null;
        this.cat.setId(catId);
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (adoptionDate == null) {
            adoptionDate = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public boolean isPending() {
        return this.status == AdoptionStatus.PENDING;
    }
    
    public boolean isApproved() {
        return this.status == AdoptionStatus.APPROVED;
    }
    
    public boolean isCompleted() {
        return this.status == AdoptionStatus.COMPLETED;
    }
    
    public boolean isRejected() {
        return this.status == AdoptionStatus.REJECTED;
    }
    
    public boolean isCancelled() {
        return this.status == AdoptionStatus.CANCELLED;
    }
    
    public boolean isActive() {
        return this.status == AdoptionStatus.PENDING || this.status == AdoptionStatus.APPROVED;
    }
    
    public String getAdopterName() {
        return user != null ? user.getFullName() : UNKNOWN;
    }
    
    public String getCatName() {
        return cat != null ? cat.getName() : UNKNOWN;
    }
    
    public String getCatBreed() {
        return cat != null && cat.getBreed() != null ? cat.getBreed().getName() : UNKNOWN;
    }
    
    public long getDaysFromApplication() {
        return java.time.temporal.ChronoUnit.DAYS.between(adoptionDate.toLocalDate(), LocalDateTime.now().toLocalDate());
    }
    
    @Override
    public String toString() {
        return "Adoption{" +
                "id=" + id +
                ", user=" + (user != null ? user.getFullName() : "null") +
                ", cat=" + (cat != null ? cat.getName() : "null") +
                ", status=" + status +
                ", adoptionDate=" + adoptionDate +
                '}';
    }
}
