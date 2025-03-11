package com.pokemonreview.api.freemarker;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class FreeMarkerConfig {

    private static final Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);

    static {
        // Set the directory for templates
        configuration.setClassForTemplateLoading(FreeMarkerConfig.class, "/templates");

        // Set the default encoding
        configuration.setDefaultEncoding("UTF-8");

        // Set how errors will appear
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // Log exceptions to stdout
        configuration.setLogTemplateExceptions(false);

        // Wrap unchecked exceptions
        configuration.setWrapUncheckedExceptions(true);
    }

    public static Configuration getConfiguration() {
        return configuration;
    }
}

