package com.kumistudy.task.dto;

import com.kumistudy.task.Task;
import java.time.LocalDateTime;

public record TaskResponse(Long id, String title, boolean completed, LocalDateTime completedAt, LocalDateTime createdAt) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.isCompleted(), task.getCompletedAt(), task.getCreatedAt());
    }
}
