package com.pokemonreview.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "profile")
@Data
@NoArgsConstructor
public class ProfileEntity {

    @Id
    private long profileId;

    @Column(name = "email", nullable = true, unique = true)
    private String email;

    @Column(name = "phoneNumber", nullable = true, unique = true)
    private String phoneNumber;

    @Column(name = "fistName", nullable = false)
    private String fistName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "avatar",nullable = true)
    private String avatar;

    @Column(name="birthDay",nullable = true)
    private String birthDay;

    @Column(name="address",nullable = true)
    private String address;

    @Column(name="sex",nullable = true)
    private String sex;

    @Column(name = "createTime", nullable = false, updatable = false)
    private long createTime;

    @Column(name = "updateTime", nullable = false)
    private long updatedTime;


}
