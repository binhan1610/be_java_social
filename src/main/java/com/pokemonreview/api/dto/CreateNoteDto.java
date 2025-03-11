package com.pokemonreview.api.dto;


import lombok.Data;

import java.util.Optional;

@Data
public class CreateNoteDto {
private String topic;
private Optional<Long> id_lable ;
private String title;
}
