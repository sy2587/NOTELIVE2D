package com.kumistudy.tag;

import com.kumistudy.auth.CurrentUserService;
import com.kumistudy.common.api.ApiResponse;
import com.kumistudy.tag.dto.TagRequest;
import com.kumistudy.tag.dto.TagResponse;
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
@RequestMapping("/api/tags")
public class TagController {
    private final TagService tagService;
    private final CurrentUserService currentUserService;

    public TagController(TagService tagService, CurrentUserService currentUserService) {
        this.tagService = tagService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public ApiResponse<List<TagResponse>> list(HttpServletRequest request) {
        return ApiResponse.success(tagService.list(currentUserService.requireUserId(request)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TagResponse>> create(@Valid @RequestBody TagRequest body, HttpServletRequest request) {
        TagResponse tag = tagService.create(currentUserService.requireUserId(request), body);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(tag));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        tagService.delete(currentUserService.requireUserId(request), id);
        return ResponseEntity.noContent().build();
    }
}
