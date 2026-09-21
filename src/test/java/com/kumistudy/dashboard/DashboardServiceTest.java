package com.kumistudy.dashboard;

import com.kumistudy.note.NoteRepository;
import com.kumistudy.subject.SubjectRepository;
import com.kumistudy.task.TaskRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {
    @Mock SubjectRepository subjectRepository;
    @Mock NoteRepository noteRepository;
    @Mock TaskRepository taskRepository;

    @Test
    void getDashboard_shouldAggregateOnlyCurrentOwnersData() {
        DashboardService service = new DashboardService(subjectRepository, noteRepository, taskRepository);
        when(subjectRepository.countByOwnerIdAndDeletedAtIsNull(7L)).thenReturn(2L);
        when(noteRepository.countByOwnerIdAndDeletedAtIsNull(7L)).thenReturn(4L);
        when(taskRepository.countByOwnerIdAndStatusAndDeletedAtIsNullAndSubject_DeletedAtIsNull(7L, "PENDING")).thenReturn(3L);
        when(noteRepository.findTop5ByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(7L)).thenReturn(List.of());
        when(taskRepository.findTop5ByOwnerIdAndStatusAndDeletedAtIsNullAndSubject_DeletedAtIsNullOrderByCreatedAtDesc(7L, "PENDING")).thenReturn(List.of());

        DashboardResponse result = service.getDashboard(7L);

        assertEquals(2L, result.subjectCount());
        assertEquals(4L, result.noteCount());
        assertEquals(3L, result.pendingTaskCount());
        verify(noteRepository).findTop5ByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(7L);
    }
}
