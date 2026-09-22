package com.kumistudy.task;

import com.kumistudy.auth.CurrentUserService;
import com.kumistudy.config.SecurityConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskOverviewController.class)
@Import(SecurityConfig.class)
class TaskOverviewControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean TaskService taskService;
    @MockBean CurrentUserService currentUserService;

    @Test
    void pending_shouldUseCurrentSessionOwner() throws Exception {
        when(currentUserService.requireUserId(any(HttpServletRequest.class))).thenReturn(7L);
        when(taskService.pending(7L)).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/tasks/pending").with(user("alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(taskService).pending(7L);
    }
}
