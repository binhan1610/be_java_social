package com.pokemonreview.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "meterreading")
@Data
public class MeterReading {
    @Id
    private long meterReadingId;

    private long workspaceId;
    private long roomId;
    private int month;
    private int year;
    private int electricOld;
    private int electricNew;
    private int waterOld;
    private int waterNew;
    private long createTime;
}