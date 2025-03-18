package com.pokemonreview.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accountId;

    @Column(name = "profileId" , nullable = false, unique = true)
    private String profileId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "token",nullable = true)
    private String token;

    @Column(name="fcm_token",nullable = true)
    private String fcm_token;

    @Column(name = "google_id", unique = true,nullable = true)
    private String googleId;

    @Column(name = "facebook_id", unique = true,nullable = true)
    private String facebookId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createTime", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updateTime", nullable = false)
    @UpdateTimestamp
    private Date updatedTime;

}
