package com.pokemonreview.api.dto;

import lombok.Data;

@Data
public class PostDto {
    private String[] tags;

    private String[] images;

    private String caption;
}
