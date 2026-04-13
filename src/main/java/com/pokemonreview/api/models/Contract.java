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
    private long contractId;

    private long workspaceId;
    private long roomId;
    private long tenantId;
    private long startDate;
    private long endDate;
    private long depositAmount;
    private String status;
    private long createTime;
    private long updateTime;
}