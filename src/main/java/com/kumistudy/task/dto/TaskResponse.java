package com.kumistudy.task.dto;

import com.kumistudy.task.Task;
import java.time.LocalDateTime;
import java.time.LocalDate;

public record TaskResponse(Long id, Long subjectId, String title, String description, String priority,
                           LocalDate dueDate, boolean dueToday, boolean overdue,
                           boolean completed, LocalDateTime completedAt, LocalDateTime createdAt) {
    public static TaskResponse from(Task task) {
        LocalDate today = LocalDate.now();
        boolean pending = !task.isCompleted();
        return new TaskResponse(task.getId(), task.getSubjectId(), task.getTitle(), task.getDescription(),
                task.getPriority(), task.getDueDate(), pending && today.equals(task.getDueDate()),
                pending && task.getDueDate() != null && task.getDueDate().isBefore(today),
                task.isCompleted(), task.getCompletedAt(), task.getCreatedAt());
    }
}
