package com.pokemonreview.api.dto;


import lombok.Data;

import java.util.Optional;

@Data
public class SaveNotiDto {
private Long user_id;
private Optional<String> topic;
private String title;
private String payload;
}
