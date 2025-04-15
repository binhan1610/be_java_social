package com.pokemonreview.api.service.impl;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.stream.Collectors;

@Converter
public class LongArrayConverter implements AttributeConverter<Long[], String> {

    private static final String SEPARATOR = ","; // Ký tự phân tách

    @Override
    public String convertToDatabaseColumn(Long[] attribute) {
        // Convert từ Long[] sang String
        if (attribute == null || attribute.length == 0) {
            return null;
        }
        return Arrays.stream(attribute)
                .map(String::valueOf)
                .collect(Collectors.joining(SEPARATOR));
    }

    @Override
    public Long[] convertToEntityAttribute(String dbData) {
        // Convert từ String sang Long[]
        if (dbData == null || dbData.trim().isEmpty()) {
            return new Long[0];
        }
        return Arrays.stream(dbData.split(SEPARATOR))
                .map(Long::valueOf)
                .toArray(Long[]::new);
    }
}

