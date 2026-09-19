package com.kumistudy.tag;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

    // TODO: Entity 完成 owner 欄位後，加入目前使用者的標籤列表與名稱查詢。
}
