package com.pokemonreview.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "profile")
@Data
@NoArgsConstructor
public class Profile {

    @Id
    private long userId;

    private long workspaceId;
    private String email;
    private String phoneNumber;
    private String fistName;
    private String lastName;
    private String fullName;
    private String avatar;
    private String birthDay;
    private String address;
    private String sex;
    private long createTime;
    private long updatedTime;


}
