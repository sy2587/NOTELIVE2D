package com.kumistudy.tag;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {

    // TODO: 注入 TagRepository，集中處理標籤建立、重複檢查與刪除。

    @Transactional
    public void create() {
        // TODO: 依目前使用者檢查同名標籤，再儲存。
    }

    public void list() {
        // TODO: 只回傳目前使用者擁有的標籤。
    }
}
