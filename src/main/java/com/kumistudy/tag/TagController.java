package com.kumistudy.tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @GetMapping
    public void list() {
        // TODO: 回傳目前使用者的標籤列表。
    }

    @PostMapping
    public void create() {
        // TODO: 接收 @Valid TagRequest，成功時回傳 TagResponse。
    }
}
