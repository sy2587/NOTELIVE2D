package com.kumistudy.task;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByOwnerIdAndSubject_IdAndDeletedAtIsNullOrderByCreatedAtAsc(Long ownerId, Long subjectId);
    Optional<Task> findByIdAndOwnerIdAndSubject_IdAndDeletedAtIsNull(Long id, Long ownerId, Long subjectId);
    List<Task> findTop5ByOwnerIdAndStatusAndDeletedAtIsNullAndSubject_DeletedAtIsNullOrderByCreatedAtDesc(Long ownerId, String status);
    long countByOwnerIdAndStatusAndDeletedAtIsNullAndSubject_DeletedAtIsNull(Long ownerId, String status);
}
