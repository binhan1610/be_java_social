package com.pokemonreview.api.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long likeId;

    @Column(name = "userId")
    private long userId;

    @Column(name = "postId")
    private long postId;

    @Column(name = "commentId")
    private long commentId;

    @Column(name = "type")
    private int type;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createTime", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updateTime", nullable = false)
    @UpdateTimestamp
    private Date updatedTime;
}
