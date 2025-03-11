package com.pokemonreview.api.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
    private String fcm_token;
}
