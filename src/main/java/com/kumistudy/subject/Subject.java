package com.kumistudy.subject;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: 加入 owner、semester、name、color、icon、teacher、classroom 等欄位。
    // TODO: owner 必須代表目前登入者，不能由前端任意指定。
    // TODO: status 與 deletedAt 用於封存及軟刪除。

    protected Subject() {
    }

    public Long getId() {
        return id;
    }
}
