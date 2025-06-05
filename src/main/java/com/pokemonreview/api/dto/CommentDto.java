package com.pokemonreview.api.dto;

import lombok.Data;

@Data
public class CommentDto {
    private long rootId;
    private String title;
    private String image;
}
