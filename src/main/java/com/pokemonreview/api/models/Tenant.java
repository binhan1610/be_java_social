package com.pokemonreview.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "tenant")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tenant {
    @Id
    private Long tenantId;

    private Long workspaceId;
    private String fullName;
    private String phone;
    private String identityCard;
    private String imageUrl;
    private long createTime;
    private long updateTime;
}