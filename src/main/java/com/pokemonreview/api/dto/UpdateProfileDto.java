package com.pokemonreview.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UpdateProfileDto {

    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String avatar;
    private String birthDay;
    private String address;
    private String sex;

}
