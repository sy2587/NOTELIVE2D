package com.kumistudy.subject;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    // TODO: Entity 補上 owner 關聯後，再建立 findAllByOwnerId 等隔離查詢。
    // TODO: 每個查詢都要帶 ownerId，避免不同使用者讀到彼此資料。
}
