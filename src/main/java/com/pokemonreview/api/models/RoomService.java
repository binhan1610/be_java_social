package com.pokemonreview.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "roomservice")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomService {
    @Id
    private Long roomServiceId;

    private Long workspaceId;
    private Long roomId;
    private Long serviceId;
}