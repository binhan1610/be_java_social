package com.pokemonreview.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "service")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCatalog {
    @Id
    private Long serviceId;

    private Long workspaceId;
    private String name;
    private double price;
    private String unit;
}