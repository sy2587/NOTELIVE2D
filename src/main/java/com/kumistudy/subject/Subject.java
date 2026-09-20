package com.kumistudy.subject;

import com.kumistudy.auth.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "semester_id")
    private Long semesterId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 20)
    private String color;

    @Column(length = 50)
    private String icon;

    @Column(name = "study_goal", length = 100)
    private String studyGoal;

    @Column(nullable = false)
    private int progress;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Subject() {
    }

    public Subject(User owner, String name, String color, String icon, String studyGoal,
                   int progress, String description) {
        this.owner = owner;
        update(name, color, icon, studyGoal, progress, description);
    }

    public void update(String name, String color, String icon, String studyGoal,
                       int progress, String description) {
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.studyGoal = studyGoal;
        this.progress = progress;
        this.description = description;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.status = "DELETED";
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getSemesterId() { return semesterId; }
    public String getName() { return name; }
    public String getColor() { return color; }
    public String getIcon() { return icon; }
    public String getStudyGoal() { return studyGoal; }
    public int getProgress() { return progress; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
