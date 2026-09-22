package com.kumistudy.note;

import com.kumistudy.auth.CurrentUserService;
import com.kumistudy.common.api.ApiResponse;
import com.kumistudy.note.dto.NoteRequest;
import com.kumistudy.note.dto.NoteResponse;
import com.kumistudy.note.dto.NotePageResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteService noteService;
    private final CurrentUserService currentUserService;
    public NoteController(NoteService noteService, CurrentUserService currentUserService) { this.noteService = noteService; this.currentUserService = currentUserService; }
    @GetMapping public ApiResponse<List<NoteResponse>> list(HttpServletRequest request) { return ApiResponse.success(noteService.list(currentUserService.requireUserId(request))); }
    @GetMapping("/search")
    public ApiResponse<NotePageResponse> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long folderId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) Boolean favorite,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        return ApiResponse.success(noteService.search(currentUserService.requireUserId(request), q, folderId, tagId, favorite, page, size));
    }
    @GetMapping("/{id}") public ApiResponse<NoteResponse> get(@PathVariable Long id, HttpServletRequest request) { return ApiResponse.success(noteService.get(currentUserService.requireUserId(request), id)); }
    @PostMapping public ResponseEntity<ApiResponse<NoteResponse>> create(@Valid @RequestBody NoteRequest body, HttpServletRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(noteService.create(currentUserService.requireUserId(request), body))); }
    @PutMapping("/{id}") public ApiResponse<NoteResponse> update(@PathVariable Long id, @Valid @RequestBody NoteRequest body, HttpServletRequest request) { return ApiResponse.success(noteService.update(currentUserService.requireUserId(request), id, body)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id, HttpServletRequest request) { noteService.delete(currentUserService.requireUserId(request), id); return ResponseEntity.noContent().build(); }
}
