package com.kumistudy.task;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @GetMapping
    public void list() {
        // TODO: 接收 date range、status 與 subjectId，回傳分頁資料。
    }

    @PostMapping
    public void create() {
        // TODO: 接收 @Valid TaskRequest。
    }

    @PatchMapping("/{id}/complete")
    public void complete(@PathVariable Long id) {
        // TODO: 呼叫 service 完成任務，避免 Controller 直接修改 Entity。
    }
}
