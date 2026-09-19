package com.kumistudy.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: 加入 username、email、passwordHash、displayName、status。
    // TODO: 加入 createdAt、updatedAt、deletedAt，並使用 JPA 欄位映射。
    // TODO: 密碼欄位只能儲存 BCrypt hash，不要保存原始密碼。

    protected User() {
        // JPA 需要無參數建構子；建立使用者請透過註冊流程。
    }

    public Long getId() {
        return id;
    }
}
