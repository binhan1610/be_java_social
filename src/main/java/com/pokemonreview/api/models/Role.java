package com.pokemonreview.api.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    private int userId;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "createTime", nullable = false, updatable = false)
    private long createTime;

    @Column(name = "updateTime", nullable = false)
    private long updatedTime;
}
