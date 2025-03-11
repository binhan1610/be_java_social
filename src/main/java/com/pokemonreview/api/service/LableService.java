package com.pokemonreview.api.service;

import com.google.api.Page;
import com.pokemonreview.api.dto.CreateLableDto;
import com.pokemonreview.api.dto.lable.LableDto;
import com.pokemonreview.api.dto.lable.ResponseLableDto;
import com.pokemonreview.api.models.Lable;

import java.util.List;

public interface LableService {
    ResponseLableDto findAllLableByUser(String username,int page,int size);
    LableDto createLableByUser(String username, String lable_name);
//    void deleteLable(int lable_id);
}
