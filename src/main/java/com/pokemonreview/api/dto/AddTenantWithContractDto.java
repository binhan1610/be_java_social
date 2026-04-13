package com.pokemonreview.api.dto;

import lombok.Data;

@Data
public class AddTenantWithContractDto {
    // Tenant info
    private String fullName;
    private String phone;
    private String identityCard;
    private String imageUrl;

    // Contract info
    private long roomId;
    private String duration;
    private long depositAmount;
    private String contractStatus;
}