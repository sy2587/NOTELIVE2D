package com.kumistudy.folder;

import com.kumistudy.auth.CurrentUserService;
import com.kumistudy.common.api.ApiResponse;
import com.kumistudy.folder.dto.FolderRequest;
import com.kumistudy.folder.dto.FolderResponse;
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
@RequestMapping("/api/folders")
public class FolderController {
    private final FolderService folderService;
    private final CurrentUserService currentUserService;
    public FolderController(FolderService folderService, CurrentUserService currentUserService) { this.folderService = folderService; this.currentUserService = currentUserService; }
    @GetMapping public ApiResponse<List<FolderResponse>> list(HttpServletRequest request) { return ApiResponse.success(folderService.list(currentUserService.requireUserId(request))); }
    @PostMapping public ResponseEntity<ApiResponse<FolderResponse>> create(@Valid @RequestBody FolderRequest body, HttpServletRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(folderService.create(currentUserService.requireUserId(request), body))); }
    @PutMapping("/{id}") public ApiResponse<FolderResponse> update(@PathVariable Long id, @Valid @RequestBody FolderRequest body, HttpServletRequest request) { return ApiResponse.success(folderService.update(currentUserService.requireUserId(request), id, body)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) { folderService.delete(currentUserService.requireUserId(request), id); return ResponseEntity.noContent().build(); }
}
