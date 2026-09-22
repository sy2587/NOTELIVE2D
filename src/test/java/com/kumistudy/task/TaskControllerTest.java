package com.kumistudy.task;

import com.kumistudy.auth.CurrentUserService;
import com.kumistudy.config.SecurityConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
class TaskControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean TaskService taskService;
    @MockBean CurrentUserService currentUserService;

    @Test
    void list_shouldUseCurrentSessionOwner() throws Exception {
        when(currentUserService.requireUserId(any(HttpServletRequest.class))).thenReturn(7L);
        when(taskService.list(7L, 2L)).thenReturn(java.util.List.of());
        mockMvc.perform(get("/api/subjects/2/tasks").with(user("alice")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
        verify(taskService).list(7L, 2L);
    }

    @Test
    void create_shouldRequireCsrfToken() throws Exception {
        mockMvc.perform(post("/api/subjects/2/tasks").with(user("alice"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Read\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_shouldValidateRequest() throws Exception {
        mockMvc.perform(post("/api/subjects/2/tasks").with(user("alice")).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    @Test
    void update_shouldRequireCsrfToken() throws Exception {
        mockMvc.perform(put("/api/subjects/2/tasks/9").with(user("alice"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Read\"}"))
                .andExpect(status().isForbidden());
    }
}
