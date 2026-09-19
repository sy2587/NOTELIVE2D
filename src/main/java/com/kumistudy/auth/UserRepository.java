package com.kumistudy.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // TODO: Entity 補上 username、email 後，再建立 findByUsername 與 findByEmail。
    // TODO: 查詢方法應該排除 deletedAt 不為 null 的帳號。
}
