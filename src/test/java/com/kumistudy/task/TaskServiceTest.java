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
}
