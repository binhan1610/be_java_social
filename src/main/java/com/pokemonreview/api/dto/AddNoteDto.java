package com.pokemonreview.api.dto;

import lombok.Data;

import java.util.Optional;

@Data
public class AddNoteDto {
    private Long note_id;
    private Optional<String> link_image;
    private Optional<String> title;
}
