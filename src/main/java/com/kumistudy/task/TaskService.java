package com.kumistudy.task;

import com.kumistudy.auth.User;
import com.kumistudy.auth.UserRepository;
import com.kumistudy.common.api.ErrorCode;
import com.kumistudy.common.exception.ApiException;
import com.kumistudy.subject.Subject;
import com.kumistudy.subject.SubjectRepository;
import com.kumistudy.task.dto.TaskRequest;
import com.kumistudy.task.dto.TaskResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, SubjectRepository subjectRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> list(Long ownerId, Long subjectId) {
        requireSubject(ownerId, subjectId);
        return taskRepository.findAllByOwnerIdAndSubject_IdAndDeletedAtIsNullOrderByCreatedAtAsc(ownerId, subjectId)
                .stream().map(TaskResponse::from).toList();
    }

    @Transactional
    public TaskResponse create(Long ownerId, Long subjectId, TaskRequest request) {
        Subject subject = requireSubject(ownerId, subjectId);
        User owner = userRepository.getReferenceById(ownerId);
        return TaskResponse.from(taskRepository.save(new Task(owner, subject, request.title().trim())));
    }

    @Transactional
    public TaskResponse toggle(Long ownerId, Long subjectId, Long taskId) {
        Task task = taskRepository.findByIdAndOwnerIdAndSubject_IdAndDeletedAtIsNull(taskId, ownerId, subjectId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "找不到此待辦事項"));
        task.toggleCompleted();
        return TaskResponse.from(task);
    }

    @Transactional
    public void delete(Long ownerId, Long subjectId, Long taskId) {
        Task task = taskRepository.findByIdAndOwnerIdAndSubject_IdAndDeletedAtIsNull(taskId, ownerId, subjectId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "找不到此待辦事項"));
        task.delete();
    }

    private Subject requireSubject(Long ownerId, Long subjectId) {
        return subjectRepository.findByIdAndOwnerIdAndDeletedAtIsNull(subjectId, ownerId)
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND, "找不到此便利貼"));
    }
}
