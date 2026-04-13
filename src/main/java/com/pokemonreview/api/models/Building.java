package com.pokemonreview.api.models;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "building")
@Data
public class Building {
    @Id
    private long buildingId;

    private long workspaceId;
    private String name;
    private String address;
}
