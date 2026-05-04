package com.pokemonreview.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
@JsonIgnoreProperties(ignoreUnknown = true)
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