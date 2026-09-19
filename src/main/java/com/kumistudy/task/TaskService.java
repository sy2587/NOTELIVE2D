package com.kumistudy.task;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    // TODO: 注入 TaskRepository 與目前登入者元件。

    @Transactional
    public void create() {
        // TODO: 驗證標題、截止日期與 priority，再建立屬於目前使用者的任務。
    }

    public void list() {
        // TODO: 依 today、week、overdue 與 status 組合查詢條件。
    }

    @Transactional
    public void complete() {
        // TODO: 只允許 owner 完成任務，更新 status 與 completedAt。
    }
}
