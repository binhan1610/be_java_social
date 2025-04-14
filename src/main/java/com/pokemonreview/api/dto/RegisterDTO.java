package com.pokemonreview.api.dto;

import lombok.Data;

@Data
public class RegisterDTO {

    // User details
    private String username;
    private String password;
    private String fcmToken;
    private String googleId;
    private String facebookId;

    // Profile details
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String avatar;
    private String birthDay;
    private String address;
}