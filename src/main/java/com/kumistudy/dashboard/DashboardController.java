package com.kumistudy.dashboard;

import com.kumistudy.auth.CurrentUserService;
import com.kumistudy.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final CurrentUserService currentUserService;

    public DashboardController(DashboardService dashboardService, CurrentUserService currentUserService) {
        this.dashboardService = dashboardService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public ApiResponse<DashboardResponse> getDashboard(HttpServletRequest request) {
        return ApiResponse.success(dashboardService.getDashboard(currentUserService.requireUserId(request)));
    }
}
