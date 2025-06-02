package com.pokemonreview.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "chat")
@Data
@NoArgsConstructor

public class ChatEntity {
    @Id()
    private long createTime;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "context")
    private String content;

    @Column(name = "id")
    private long id;

    @Column(name = "updateTime")
    private long updateTime;
}
