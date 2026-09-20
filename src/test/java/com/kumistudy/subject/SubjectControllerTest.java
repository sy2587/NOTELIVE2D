package com.kumistudy.subject;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubjectController.class)
@Import(SecurityConfig.class)
class SubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubjectService subjectService;

    @MockBean
    private CurrentUserService currentUserService;

    @Test
    void list_shouldUseCurrentSessionOwner() throws Exception {
        when(currentUserService.requireUserId(any(HttpServletRequest.class))).thenReturn(7L);
        when(subjectService.list(7L)).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/subjects").with(user("alice")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(subjectService).list(7L);
    }

    @Test
    void create_shouldRequireCsrfToken() throws Exception {
        mockMvc.perform(post("/api/subjects")
                        .with(user("alice"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"資料結構\",\"color\":\"#6F9987\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
    }

    @Test
    void create_shouldValidateRequest() throws Exception {
        mockMvc.perform(post("/api/subjects")
                        .with(user("alice"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"color\":\"red\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }
}
