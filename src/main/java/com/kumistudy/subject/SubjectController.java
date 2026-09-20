package com.kumistudy.subject;

import com.kumistudy.auth.CurrentUserService;
import com.kumistudy.common.api.ApiResponse;
import com.kumistudy.subject.dto.SubjectRequest;
import com.kumistudy.subject.dto.SubjectResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;
    private final CurrentUserService currentUserService;

    public SubjectController(SubjectService subjectService, CurrentUserService currentUserService) {
        this.subjectService = subjectService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public ApiResponse<List<SubjectResponse>> list(HttpServletRequest request) {
        return ApiResponse.success(subjectService.list(currentUserService.requireUserId(request)));
    }

    @GetMapping("/{id}")
    public ApiResponse<SubjectResponse> get(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success(subjectService.get(currentUserService.requireUserId(request), id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubjectResponse>> create(
            @Valid @RequestBody SubjectRequest body,
            HttpServletRequest request) {
        SubjectResponse subject = subjectService.create(currentUserService.requireUserId(request), body);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(subject));
    }

    @PutMapping("/{id}")
    public ApiResponse<SubjectResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SubjectRequest body,
            HttpServletRequest request) {
        return ApiResponse.success(subjectService.update(currentUserService.requireUserId(request), id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        subjectService.delete(currentUserService.requireUserId(request), id);
        return ResponseEntity.noContent().build();
    }
}
