package io.github.czerepko.i18n.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

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
            PROPERTIES.load(Optional.ofNullable(I18nProperties.class.getClassLoader().getResource(PROPERTIES_FILENAME))
                                    .orElseThrow(() -> new FileNotFoundException("Could not find properties resource file " + PROPERTIES_FILENAME))
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
