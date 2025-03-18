package com.pokemonreview.api.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentId;

    @Column(name = "userId")
    private long userId;

    @Column(name = "postId")
    private long postId;

    @Column(name = "reCommentId")
    private long reCommentId;

    @Column(name = "image")
    private String image;

    @Column(name = "title")
    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createTime", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updateTime", nullable = false)
    @UpdateTimestamp
    private Date updatedTime;
}
