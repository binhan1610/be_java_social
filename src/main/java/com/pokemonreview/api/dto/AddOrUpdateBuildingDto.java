package com.pokemonreview.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AddOrUpdateBuildingDto {
    private String name;
    private String address;
}