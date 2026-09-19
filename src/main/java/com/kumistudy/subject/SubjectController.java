package com.kumistudy.subject;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public void list() {
        // TODO: 使用 Pageable 與查詢參數，回傳 PageResponse<SubjectResponse>。
    }

    @PostMapping
    public void create() {
        // TODO: 接收 @Valid SubjectRequest，交給 service，不要直接操作 Repository。
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id) {
        // TODO: 將 id 與 DTO 傳給 service，service 必須檢查 owner 權限。
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        // TODO: 刪除前確認資源屬於目前登入者。
    }
}
