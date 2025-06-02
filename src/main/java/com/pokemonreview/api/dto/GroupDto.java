package com.pokemonreview.api.dto;

import lombok.Data;

@Data
public class GroupDto {
    private String name;
    private String groupAvatar;
    private String groupBackground;
    private String description;
    private int type;
}