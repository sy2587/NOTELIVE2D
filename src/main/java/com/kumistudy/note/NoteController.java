package com.kumistudy.note;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public void search() {
        // TODO: 接收 keyword、subjectId、tagId、favorite、page、size 等查詢參數。
    }

    @GetMapping("/{id}")
    public void getById(@PathVariable Long id) {
        // TODO: 回傳 NoteResponse，不要直接回傳 Note entity。
    }

    @PostMapping
    public void create() {
        // TODO: 接收 @Valid NoteRequest，建立筆記並處理標籤關聯。
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id) {
        // TODO: 更新前由 service 驗證 owner 權限。
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        // TODO: 執行軟刪除並回傳一致的 API 格式。
    }
}
