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
    private long serviceId;

    private long workspaceId;
    private String name;
    private long price;
    private String unit;
}