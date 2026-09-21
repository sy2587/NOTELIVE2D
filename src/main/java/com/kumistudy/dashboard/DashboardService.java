package com.kumistudy.dashboard;

import com.kumistudy.note.NoteRepository;
import com.kumistudy.note.dto.NoteResponse;
import com.kumistudy.subject.SubjectRepository;
import com.kumistudy.task.TaskRepository;
import com.kumistudy.task.dto.TaskResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {
    private final SubjectRepository subjectRepository;
    private final NoteRepository noteRepository;
    private final TaskRepository taskRepository;

    public DashboardService(SubjectRepository subjectRepository, NoteRepository noteRepository, TaskRepository taskRepository) {
        this.subjectRepository = subjectRepository;
        this.noteRepository = noteRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Long ownerId) {
        return new DashboardResponse(
                subjectRepository.countByOwnerIdAndDeletedAtIsNull(ownerId),
                noteRepository.countByOwnerIdAndDeletedAtIsNull(ownerId),
                taskRepository.countByOwnerIdAndStatusAndDeletedAtIsNullAndSubject_DeletedAtIsNull(ownerId, "PENDING"),
                noteRepository.findTop5ByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(ownerId)
                        .stream().map(NoteResponse::from).toList(),
                taskRepository.findTop5ByOwnerIdAndStatusAndDeletedAtIsNullAndSubject_DeletedAtIsNullOrderByCreatedAtDesc(ownerId, "PENDING")
                        .stream().map(TaskResponse::from).toList()
        );
    }
}
