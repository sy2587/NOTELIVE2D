package com.kumistudy.folder.dto;

import com.kumistudy.folder.Folder;
import java.time.LocalDateTime;

public record FolderResponse(Long id, String name, LocalDateTime createdAt) {
    public static FolderResponse from(Folder folder) {
        return new FolderResponse(folder.getId(), folder.getName(), folder.getCreatedAt());
    }
}
