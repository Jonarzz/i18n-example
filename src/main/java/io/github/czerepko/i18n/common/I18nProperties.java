package io.github.czerepko.i18n.common;

import com.google.common.io.Resources;

import java.io.IOException;
import java.util.Properties;

@SuppressWarnings("UnstableApiUsage")
public enum I18nProperties {

    FILE_FORMAT("i18n.file.format"),
    FILE_ENCODING("i18n.file.encoding"),
    LANGUAGE_CODES("i18n.language.codes"),
    PLACEHOLDER_TYPE("i18n.placeholder.type");

    private static final String PROPERTIES_FILENAME = "application.properties";
    private static final Properties PROPERTIES = new Properties();

    private final String placeholder;

    static {
        reload();
    }

    I18nProperties(String placeholder) {
        this.placeholder = placeholder;
    }

    public static void reload() {
        try {
            PROPERTIES.load(Resources.getResource(PROPERTIES_FILENAME)
                                     .openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPropertyPlaceholder() {
        return placeholder;
    }

    public String getValue() {
        return PROPERTIES.getProperty(placeholder);
    }

}
