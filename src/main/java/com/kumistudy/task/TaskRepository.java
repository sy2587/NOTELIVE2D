package com.kumistudy.task;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // TODO: 加入依 ownerId、dueDate、status 的查詢，支援今日待辦與逾期任務。
}
