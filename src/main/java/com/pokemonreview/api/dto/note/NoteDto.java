package com.pokemonreview.api.dto.note;

import com.pokemonreview.api.dto.lable.LableDto;
import com.pokemonreview.api.models.Image;
import com.pokemonreview.api.models.Title;
import lombok.Data;

import java.util.List;

@Data
public class NoteDto {
    private long id;
    private String topic;
    private List<Image> imageList;
    private List<Title> titleList;
    private LableDto lable;
    private boolean important;
    private boolean success;
}
