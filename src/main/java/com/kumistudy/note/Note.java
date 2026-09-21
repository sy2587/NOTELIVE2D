package com.kumistudy.note;

import com.kumistudy.auth.User;
import com.kumistudy.folder.Folder;
import com.kumistudy.tag.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "notes")
public class Note {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "owner_id", nullable = false) private User owner;
    @Column(nullable = false, length = 255) private String title;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "folder_id") private Folder folder;
    @Column(nullable = false, columnDefinition = "LONGTEXT") private String content;
    @Column(name = "content_format", nullable = false, length = 20) private String contentFormat = "PLAIN_TEXT";
    @Column(name = "is_favorite", nullable = false) private boolean favorite;
    @Column(name = "is_pinned", nullable = false) private boolean pinned;
    @Column(nullable = false, length = 20) private String status = "ACTIVE";
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @Column(name = "deleted_at") private LocalDateTime deletedAt;
    @ManyToMany
    @JoinTable(name = "note_tags", joinColumns = @JoinColumn(name = "note_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new LinkedHashSet<>();

    protected Note() { }
    public Note(User owner, String title, String content, boolean favorite, boolean pinned) { this.owner = owner; update(title, content, favorite, pinned); }
    public void update(String title, String content, boolean favorite, boolean pinned) { this.title = title; this.content = content; this.favorite = favorite; this.pinned = pinned; }
    public void replaceTags(Set<Tag> tags) { this.tags.clear(); this.tags.addAll(tags); }
    public void moveToFolder(Folder folder) { this.folder = folder; }
    public void delete() { status = "DELETED"; deletedAt = LocalDateTime.now(); }
    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); updatedAt = createdAt; }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public boolean isFavorite() { return favorite; }
    public boolean isPinned() { return pinned; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Set<Tag> getTags() { return Set.copyOf(tags); }
    public Folder getFolder() { return folder; }
}
