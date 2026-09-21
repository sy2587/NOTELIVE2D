package com.kumistudy.tag.dto;

import com.kumistudy.tag.Tag;
import java.time.LocalDateTime;

public record TagResponse(Long id, String name, LocalDateTime createdAt) {
    public static TagResponse from(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName(), tag.getCreatedAt());
    }
}
