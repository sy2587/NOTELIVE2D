package com.kumistudy.note.dto;

import com.kumistudy.note.Note;
import com.kumistudy.folder.dto.FolderResponse;
import com.kumistudy.tag.dto.TagResponse;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record NoteResponse(Long id, String title, String content, boolean favorite, boolean pinned, List<TagResponse> tags, FolderResponse folder, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static NoteResponse from(Note note) {
        List<TagResponse> tags = note.getTags().stream().map(TagResponse::from)
                .sorted(Comparator.comparing(TagResponse::name, String.CASE_INSENSITIVE_ORDER)).toList();
        FolderResponse folder = note.getFolder() == null ? null : FolderResponse.from(note.getFolder());
        return new NoteResponse(note.getId(), note.getTitle(), note.getContent(), note.isFavorite(), note.isPinned(), tags, folder, note.getCreatedAt(), note.getUpdatedAt());
    }
}
