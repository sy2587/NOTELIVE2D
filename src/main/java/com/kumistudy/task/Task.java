package com.kumistudy.task;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: 加入 owner、subject、title、description、priority、status、dueDate。
    // TODO: 完成時記錄 completedAt；刪除時先使用 deletedAt 軟刪除。
    // TODO: 子任務與重複任務不在 MVP，先不要提前設計欄位。

    protected Task() {
    }
}
