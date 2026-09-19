package com.kumistudy.note;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

    // TODO: 建立帶 ownerId 的列表、單筆、搜尋與分頁查詢。
    // TODO: 搜尋條件包含 title/content、subject、tag、favorite 與日期範圍。
}
