package com.pokemonreview.api.dto;

import lombok.Data;

@Data
public class ChatDto {
    private String content;
    private long id;
    private long userId;
}
