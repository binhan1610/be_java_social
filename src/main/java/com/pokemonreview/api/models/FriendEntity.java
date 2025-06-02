package com.pokemonreview.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "friend")
@Data
@NoArgsConstructor
public class FriendEntity {
    @Id
    private long id;

    @Column(name = "friend_id")
    private long friendId;

    @Column(name = "status")
    private int status;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "createTime", nullable = false, updatable = false)
    private long createTime;

    @Column(name = "updateTime", nullable = false)
    private long updatedTime;
}
