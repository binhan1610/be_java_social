package com.pokemonreview.api.dto;

import lombok.Data;

@Data
public class AddOrUpdateRoomDto {
    private String name;
    private long price;
    private String status;
    private int maxCapacity;
    private Long buildingId;
}