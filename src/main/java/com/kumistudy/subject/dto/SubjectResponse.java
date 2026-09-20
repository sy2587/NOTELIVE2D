package com.kumistudy.subject.dto;

import com.kumistudy.subject.Subject;
import java.time.LocalDateTime;

public record SubjectResponse(
        Long id,
        Long semesterId,
        String name,
        String color,
        String icon,
        String studyGoal,
        int progress,
        String description,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static SubjectResponse from(Subject subject) {
        return new SubjectResponse(
                subject.getId(), subject.getSemesterId(), subject.getName(), subject.getColor(),
                subject.getIcon(), subject.getStudyGoal(), subject.getProgress(),
                subject.getDescription(), subject.getStatus(), subject.getCreatedAt(), subject.getUpdatedAt()
        );
    }
}
