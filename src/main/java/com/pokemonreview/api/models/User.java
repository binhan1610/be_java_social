package com.pokemonreview.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
public class User {

    @Id
    private long userId;

    private long workspaceId;
    private String username;
    private String password;
    private String token;
    private String fcmToken;
    private long createTime;
    private long updatedTime;

}
