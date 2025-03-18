package com.pokemonreview.api.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postId;

    @Column(name = "lables")
    private List<String> lables;

    @Column(name = "tags")
    private List<Long> tags;

    @Column(name = "images")
    private List<String> images;

    @Column(name = "caption")
    private String caption;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createTime", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updateTime", nullable = false)
    @UpdateTimestamp
    private Date updatedTime;
}
