package com.kumistudy.task;

import com.kumistudy.auth.CurrentUserService;
import com.kumistudy.common.api.ApiResponse;
import com.kumistudy.task.dto.TaskRequest;
import com.kumistudy.task.dto.TaskResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subjects/{subjectId}/tasks")
public class TaskController {
    private final TaskService taskService;
    private final CurrentUserService currentUserService;

    public TaskController(TaskService taskService, CurrentUserService currentUserService) {
        this.taskService = taskService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public ApiResponse<List<TaskResponse>> list(@PathVariable Long subjectId, HttpServletRequest request) {
        return ApiResponse.success(taskService.list(currentUserService.requireUserId(request), subjectId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> create(@PathVariable Long subjectId, @Valid @RequestBody TaskRequest body, HttpServletRequest request) {
        TaskResponse task = taskService.create(currentUserService.requireUserId(request), subjectId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(task));
    }

    @PostMapping("/{taskId}/toggle")
    public ApiResponse<TaskResponse> toggle(@PathVariable Long subjectId, @PathVariable Long taskId, HttpServletRequest request) {
        return ApiResponse.success(taskService.toggle(currentUserService.requireUserId(request), subjectId, taskId));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> delete(@PathVariable Long subjectId, @PathVariable Long taskId, HttpServletRequest request) {
        taskService.delete(currentUserService.requireUserId(request), subjectId, taskId);
        return ResponseEntity.noContent().build();
    }
}
