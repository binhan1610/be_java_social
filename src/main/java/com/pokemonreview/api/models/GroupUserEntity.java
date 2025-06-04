package com.pokemonreview.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "groupusers")
@Data
@NoArgsConstructor
public class GroupUserEntity {

    @Id
    private long groupId;

    @Column(name = "userId")
    private long userId;

    @Column(name = "name")
    private String name;

    @Column(name = "groupAvatar")
    private String groupAvatar;

    @Column(name = "groupBackground")
    private String groupBackground;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private int type;

    @Column(name = "totalMember")
    private int totalMember;

    @Column(name = "createTime", nullable = false, updatable = false)
    private long createTime;

    @Column(name = "updateTime", nullable = false)
    private long updatedTime;
}
