package com.pokemonreview.api.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Set;

@Service
public class ValidatorService
{
    private JsonSchema schema;
    private ObjectMapper mapper;

    public Set<ValidationMessage> validate(String filePath, String jsonString) throws Exception {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        try (InputStream inputStream = getClass().getResourceAsStream("/" + filePath + ".json")) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Schema file not found");
            }
            this.schema = factory.getSchema(inputStream);
        }
        this.mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);
        return schema.validate(jsonNode);
    }

}
