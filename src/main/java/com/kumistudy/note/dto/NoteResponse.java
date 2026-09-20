package com.kumistudy.note.dto;

import com.kumistudy.note.Note;
import java.time.LocalDateTime;

public record NoteResponse(Long id, String title, String content, boolean favorite, boolean pinned, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static NoteResponse from(Note note) { return new NoteResponse(note.getId(), note.getTitle(), note.getContent(), note.isFavorite(), note.isPinned(), note.getCreatedAt(), note.getUpdatedAt()); }
}
