package io.github.czerepko.i18n.translation;

import com.google.common.io.Resources;

import java.io.IOException;
import java.util.Properties;

@SuppressWarnings("UnstableApiUsage")
enum TranslationProperties {
    FILE_FORMAT("i18n.file.format"),
    LANGUAGE_CODES("i18n.language.codes"),
    PLACEHOLDER_TYPE("i18n.placeholder.type");

    private static final String PROPERTIES_FILENAME = "application.properties";

    private Properties properties = new Properties();
    private String value;

    {
        try {
            properties.load(Resources.getResource(PROPERTIES_FILENAME).openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    TranslationProperties(String placeholder) {
        value = properties.getProperty(placeholder);
    }

    String getValue() {
        return value;
    }

}