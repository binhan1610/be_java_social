package com.pokemonreview.api.service.impl;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;

@Converter
public class StringArrayConverter implements AttributeConverter<String[], String> {

    private static final String SEPARATOR = ","; // Ký tự phân tách

    @Override
    public String convertToDatabaseColumn(String[] attribute) {
        // Convert từ String[] sang String
        if (attribute == null || attribute.length == 0) {
            return null;
        }
        return String.join(SEPARATOR, attribute);
    }

    @Override
    public String[] convertToEntityAttribute(String dbData) {
        // Convert từ String sang String[]
        if (dbData == null || dbData.trim().isEmpty()) {
            return new String[0];
        }
        return dbData.split(SEPARATOR);
    }
}