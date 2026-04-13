package com.pokemonreview.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contract")
@Data
public class Contract {
    @Id
    private Long contractId;

    private Long workspaceId;
    private Long roomId;
    private Long tenantId;
    private long startDate;
    private long endDate;
    private double depositAmount;
    private String status;
    private long createTime;
    private long updateTime;
}