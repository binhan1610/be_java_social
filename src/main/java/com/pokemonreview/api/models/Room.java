package com.pokemonreview.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "room")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    @Id
    private long roomId;

    private long workspaceId;
    private long buildingId;
    private String name;
    private long price;
    private String status;
    private int maxCapacity;
    private long createTime;
    private long updateTime;
}