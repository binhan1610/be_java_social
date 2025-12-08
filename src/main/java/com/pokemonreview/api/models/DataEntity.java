package com.pokemonreview.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "data")
@Data
@NoArgsConstructor
public class DataEntity {

    @Id
    private long id;

    @Column(name = "topic", nullable = true)
    private String topic;

    @Column(name = "data", columnDefinition = "TEXT", nullable = true)
    private String data;

}
