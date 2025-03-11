package com.pokemonreview.api.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Map;


@Service
public class FreemarkerService {

    private static final Configuration CONFIGURATION = new Configuration(Configuration.VERSION_2_3_0);

    public static String generateJsonByTemplate(String templateName, Map<String, Object> input) throws Exception {
        try {
            Template template = CONFIGURATION.getTemplate(templateName);
            StringWriter writer = new StringWriter();
            template.process(input, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new Exception("Processing failed for template '" + templateName  + "' with error: " + e.getMessage(), e);
        }
    }
}

