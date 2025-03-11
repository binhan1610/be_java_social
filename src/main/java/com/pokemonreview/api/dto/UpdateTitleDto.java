package com.pokemonreview.api.dto;

import lombok.Data;

@Data
public class UpdateTitleDto {
    private Long note_id;
    private Long title_id;
    private String title;
}
