package com.pokemonreview.api.dto;

import lombok.Data;

@Data
public class RegisterDTO {

    // User details
    private String username;
    private String password;
    // Profile details
    private String firstName;
    private String lastName;
    private String sex;
    private String birthDay;
}