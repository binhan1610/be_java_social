package com.pokemonreview.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.Json;
import com.pokemonreview.api.freemarker.FreeMarkerConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Map;

@Service
public class TemplateService {

    private final Configuration configuration;
    private final ObjectMapper objectMapper;

    public TemplateService() {
        this.configuration = FreeMarkerConfig.getConfiguration();
        this.objectMapper = new ObjectMapper();
    }

    public JsonNode generateJsonFromTemplate(String templateName, Map<String, Object> data) throws Exception {
        Template template = configuration.getTemplate(templateName);
        try (StringWriter writer = new StringWriter()) {
            template.process(data, writer);
            String jsonString =  writer.toString();
             return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            throw new Exception("Error processing template", e);
        }
    }

}








































