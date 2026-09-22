package com.kumistudy.task;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByOwnerIdAndSubject_IdAndDeletedAtIsNullOrderByCreatedAtAsc(Long ownerId, Long subjectId);
    Optional<Task> findByIdAndOwnerIdAndSubject_IdAndDeletedAtIsNull(Long id, Long ownerId, Long subjectId);
    List<Task> findAllByOwnerIdAndStatusAndDeletedAtIsNullAndSubject_DeletedAtIsNullOrderByDueDateAscCreatedAtDesc(Long ownerId, String status);
    List<Task> findTop5ByOwnerIdAndStatusAndDeletedAtIsNullAndSubject_DeletedAtIsNullOrderByCreatedAtDesc(Long ownerId, String status);
    long countByOwnerIdAndStatusAndDeletedAtIsNullAndSubject_DeletedAtIsNull(Long ownerId, String status);
    long countByOwnerIdAndStatusAndDueDateAndDeletedAtIsNullAndSubject_DeletedAtIsNull(Long ownerId, String status, LocalDate dueDate);
    long countByOwnerIdAndStatusAndDueDateBeforeAndDeletedAtIsNullAndSubject_DeletedAtIsNull(Long ownerId, String status, LocalDate dueDate);
    List<Task> findAllByOwnerIdAndStatusAndDueDateAndDeletedAtIsNullAndSubject_DeletedAtIsNullOrderByCreatedAtAsc(Long ownerId, String status, LocalDate dueDate);
    List<Task> findAllByOwnerIdAndStatusAndDueDateBeforeAndDeletedAtIsNullAndSubject_DeletedAtIsNullOrderByDueDateAsc(Long ownerId, String status, LocalDate dueDate);
}
