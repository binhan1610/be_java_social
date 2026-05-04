package com.pokemonreview.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
public class PagedResponse<T> {
    private java.util.List<T> items;
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;

    public static <T> PagedResponse<T> from(Page<T> source) {
        return new PagedResponse<>(
                source.getContent(),
                source.getNumber(),
                source.getSize(),
                source.getTotalElements(),
                source.getTotalPages()
        );
    }
}
