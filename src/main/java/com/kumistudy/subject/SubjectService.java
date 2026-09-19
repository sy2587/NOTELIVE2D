package com.kumistudy.subject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Transactional
    public void create() {
        // TODO: 驗證輸入、取得目前使用者、建立 Subject 並儲存。
    }

    public void list() {
        // TODO: 依目前使用者、學期與封存狀態查詢，可加入分頁。
    }

    @Transactional
    public void update() {
        // TODO: 先用 ownerId + subjectId 查詢，再更新允許修改的欄位。
    }

    @Transactional
    public void delete() {
        // TODO: 先確認資料屬於目前使用者，再執行軟刪除或封存。
    }
}
