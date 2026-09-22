package com.kumistudy.note.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record NotePageResponse(
        List<NoteResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static NotePageResponse from(Page<NoteResponse> result) {
        return new NotePageResponse(result.getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }
}
