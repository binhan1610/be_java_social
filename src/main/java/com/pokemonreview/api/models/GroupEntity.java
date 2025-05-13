package com.pokemonreview.api.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "group")
public class GroupEntity {
    @Id
    private long id;

    @Column(name = "status")
    private int status;

    @Column(name = "group_id")
    private long userId;

    @Column(name = "createTime", nullable = false, updatable = false)
    private long createTime;

    @Column(name = "updateTime", nullable = false)
    private long updatedTime;
}
