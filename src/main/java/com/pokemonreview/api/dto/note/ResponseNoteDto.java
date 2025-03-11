package com.pokemonreview.api.dto.note;

import com.pokemonreview.api.dto.lable.LableDto;
import com.pokemonreview.api.models.Image;
import com.pokemonreview.api.models.Title;
import lombok.Data;

import java.util.List;


@Data
public class ResponseNoteDto {
    private List<NoteDto> content;
    private int pageNo;
    private int pageSize;
    private Long total;
    private int totalPages;
}
