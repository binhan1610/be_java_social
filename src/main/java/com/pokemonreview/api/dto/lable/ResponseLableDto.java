package com.pokemonreview.api.dto.lable;

import lombok.Data;

import java.util.List;


@Data
public class ResponseLableDto {
  private List<LableDto> content;
  private int pageNo;
  private int pageSize;
  private int total;
  private int totalPages;
}
