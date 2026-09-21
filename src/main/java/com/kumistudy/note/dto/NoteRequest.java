package com.kumistudy.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record NoteRequest(
        @NotBlank(message = "筆記標題不可空白") @Size(max = 255, message = "筆記標題不可超過 255 個字") String title,
        @NotBlank(message = "筆記內容不可空白") @Size(max = 10000, message = "筆記內容不可超過 10000 個字") String content,
        boolean favorite,
        boolean pinned,
        Set<Long> tagIds,
        Long folderId
) { }
