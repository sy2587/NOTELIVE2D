package com.kumistudy.task;

import com.kumistudy.auth.CurrentUserService;
import com.kumistudy.common.api.ApiResponse;
import com.kumistudy.task.dto.TaskResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskOverviewController {
    private final TaskService taskService;
    private final CurrentUserService currentUserService;

    public TaskOverviewController(TaskService taskService, CurrentUserService currentUserService) {
        this.taskService = taskService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/pending")
    public ApiResponse<List<TaskResponse>> pending(HttpServletRequest request) {
        return ApiResponse.success(taskService.pending(currentUserService.requireUserId(request)));
    }

    @GetMapping("/today")
    public ApiResponse<List<TaskResponse>> today(HttpServletRequest request) {
        return ApiResponse.success(taskService.today(currentUserService.requireUserId(request)));
    }

    @GetMapping("/overdue")
    public ApiResponse<List<TaskResponse>> overdue(HttpServletRequest request) {
        return ApiResponse.success(taskService.overdue(currentUserService.requireUserId(request)));
    }
}
