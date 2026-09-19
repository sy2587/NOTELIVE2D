package com.kumistudy.note;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: 加入 owner、subject、folder、title、content、contentFormat。
    // TODO: 加入 favorite、pinned、status、createdAt、updatedAt、deletedAt。
    // TODO: 標籤關聯可使用 NoteTag entity 或 ManyToMany，但要避免直接暴露 Entity。

    protected Note() {
    }

    public Long getId() {
        return id;
    }
}
