package com.kumistudy.task;

import com.kumistudy.auth.User;
import com.kumistudy.auth.UserRepository;
import com.kumistudy.common.api.ErrorCode;
import com.kumistudy.common.exception.ApiException;
import com.kumistudy.subject.Subject;
import com.kumistudy.subject.SubjectRepository;
import com.kumistudy.task.dto.TaskRequest;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock TaskRepository taskRepository;
    @Mock SubjectRepository subjectRepository;
    @Mock UserRepository userRepository;

    @Test
    void create_shouldRequireAnOwnedSubject() {
        TaskService service = new TaskService(taskRepository, subjectRepository, userRepository);
        when(subjectRepository.findByIdAndOwnerIdAndDeletedAtIsNull(2L, 7L)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> service.create(7L, 2L, new TaskRequest("Read chapter")));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void list_shouldFilterByOwnerAndSubject() {
        TaskService service = new TaskService(taskRepository, subjectRepository, userRepository);
        User owner = new User("alice", "a@example.com", "hash", "Alice");
        Subject subject = new Subject(owner, "Java", "#fff", "book", null, 0, null);
        when(subjectRepository.findByIdAndOwnerIdAndDeletedAtIsNull(2L, 7L)).thenReturn(Optional.of(subject));
        when(taskRepository.findAllByOwnerIdAndSubject_IdAndDeletedAtIsNullOrderByCreatedAtAsc(7L, 2L)).thenReturn(List.of());

        assertEquals(List.of(), service.list(7L, 2L));
        verify(taskRepository).findAllByOwnerIdAndSubject_IdAndDeletedAtIsNullOrderByCreatedAtAsc(7L, 2L);
    }

    @Test
    void create_shouldTrimTitle() {
        TaskService service = new TaskService(taskRepository, subjectRepository, userRepository);
        User owner = new User("alice", "a@example.com", "hash", "Alice");
        Subject subject = new Subject(owner, "Java", "#fff", "book", null, 0, null);
        when(subjectRepository.findByIdAndOwnerIdAndDeletedAtIsNull(2L, 7L)).thenReturn(Optional.of(subject));
        when(userRepository.getReferenceById(7L)).thenReturn(owner);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals("Read chapter", service.create(7L, 2L, new TaskRequest("  Read chapter  ")).title());
    }

    @Test
    void create_shouldSavePriorityDescriptionAndDueDate() {
        TaskService service = new TaskService(taskRepository, subjectRepository, userRepository);
        User owner = new User("alice", "a@example.com", "hash", "Alice");
        Subject subject = new Subject(owner, "Java", "#fff", "book", null, 0, null);
        LocalDate dueDate = LocalDate.now().plusDays(1);
        when(subjectRepository.findByIdAndOwnerIdAndDeletedAtIsNull(2L, 7L)).thenReturn(Optional.of(subject));
        when(userRepository.getReferenceById(7L)).thenReturn(owner);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.create(7L, 2L,
                new TaskRequest("Read", "  Chapter 3  ", "HIGH", dueDate));

        assertEquals("Chapter 3", result.description());
        assertEquals("HIGH", result.priority());
        assertEquals(dueDate, result.dueDate());
    }

    @Test
    void today_shouldOnlyQueryCurrentOwnersPendingTasks() {
        TaskService service = new TaskService(taskRepository, subjectRepository, userRepository);
        LocalDate today = LocalDate.now();
        when(taskRepository.findAllByOwnerIdAndStatusAndDueDateAndDeletedAtIsNullAndSubject_DeletedAtIsNullOrderByCreatedAtAsc(
                7L, "PENDING", today)).thenReturn(List.of());

        assertEquals(List.of(), service.today(7L));
        verify(taskRepository).findAllByOwnerIdAndStatusAndDueDateAndDeletedAtIsNullAndSubject_DeletedAtIsNullOrderByCreatedAtAsc(
                7L, "PENDING", today);
    }

    @Test
    void pending_shouldOnlyQueryCurrentOwnersActiveSubjects() {
        TaskService service = new TaskService(taskRepository, subjectRepository, userRepository);
        when(taskRepository.findAllByOwnerIdAndStatusAndDeletedAtIsNullAndSubject_DeletedAtIsNullOrderByDueDateAscCreatedAtDesc(
                7L, "PENDING")).thenReturn(List.of());

        assertEquals(List.of(), service.pending(7L));
        verify(taskRepository).findAllByOwnerIdAndStatusAndDeletedAtIsNullAndSubject_DeletedAtIsNullOrderByDueDateAscCreatedAtDesc(
                7L, "PENDING");
    }

    @Test
    void update_shouldOnlyEditOwnedTaskInSubject() {
        TaskService service = new TaskService(taskRepository, subjectRepository, userRepository);
        User owner = new User("alice", "a@example.com", "hash", "Alice");
        Subject subject = new Subject(owner, "Java", "#fff", "book", null, 0, null);
        Task task = new Task(owner, subject, "Old");
        LocalDate dueDate = LocalDate.now().plusDays(2);
        when(taskRepository.findByIdAndOwnerIdAndSubject_IdAndDeletedAtIsNull(9L, 7L, 2L))
                .thenReturn(Optional.of(task));

        var result = service.update(7L, 2L, 9L,
                new TaskRequest("  New title  ", null, "LOW", dueDate));

        assertEquals("New title", result.title());
        assertEquals("LOW", result.priority());
        assertEquals(dueDate, result.dueDate());
        verify(taskRepository).findByIdAndOwnerIdAndSubject_IdAndDeletedAtIsNull(9L, 7L, 2L);
    }
}
