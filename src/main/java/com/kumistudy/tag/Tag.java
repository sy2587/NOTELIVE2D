package com.kumistudy.tag;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: 加入 owner、name、createdAt、updatedAt。
    // TODO: tag 名稱唯一性要以 owner + name 判斷，而不是全站共用。

    protected Tag() {
    }
}
