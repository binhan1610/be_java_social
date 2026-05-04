package com.pokemonreview.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AddOrUpdateRoomDto {
    private String name;
    private long price;
    private String status;
    private int maxCapacity;
    private Long buildingId;
}