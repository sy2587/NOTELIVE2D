package com.kumistudy.note;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Transactional
    public void create() {
        // TODO: 驗證標題與內容，使用目前使用者建立筆記。
    }

    public void search() {
        // TODO: 組合搜尋條件，回傳分頁結果與必要的關鍵字標示資訊。
    }

    public void getById() {
        // TODO: 使用 noteId + currentUserId 查詢，找不到時拋出資源不存在例外。
    }

    @Transactional
    public void update() {
        // TODO: 僅允許 owner 修改，更新後保留 updatedAt。
    }

    @Transactional
    public void delete() {
        // TODO: 先做軟刪除；回收桶與還原功能之後再補齊。
    }
}
