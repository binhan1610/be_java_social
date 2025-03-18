package com.pokemonreview.api.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long profileId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phoneNumber", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "fistName", nullable = false, unique = true)
    private String fistName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "avatar",nullable = true)
    private String avatar;

    @Column(name="birthDay",nullable = true)
    private String birthDay;

    @Column(name="address",nullable = true)
    private String address;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createTime", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updateTime", nullable = false)
    @UpdateTimestamp
    private Date updatedTime;


}
