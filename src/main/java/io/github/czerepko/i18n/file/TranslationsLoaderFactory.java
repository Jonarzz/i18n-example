package io.github.czerepko.i18n.file;

import java.util.Map;

public enum TranslationsLoaderFactory {
    JSON(new JsonTranslationsLoader()),
    YAML(new YamlTranslationsLoader()),
    PROPERTIES(new PropertiesTranslationsLoader());

    private static final Map<String, TranslationsLoaderFactory> STRING_TYPE_NAME_TO_FACTORY_TYPE = Map.of(
            "json",       JSON,
            "yaml",       YAML,
            "yml",        YAML,
            "properties", PROPERTIES,
            "property",   PROPERTIES
    );

    private I18nTranslationsLoader translationsLoader;

    TranslationsLoaderFactory(I18nTranslationsLoader translationsLoader) {
        this.translationsLoader = translationsLoader;
    }

    public I18nTranslationsLoader create() {
        return translationsLoader;
    }

    public static TranslationsLoaderFactory fromString(String typeName) {
        if (!STRING_TYPE_NAME_TO_FACTORY_TYPE.containsKey(typeName)) {
            throw new InvalidTranslationsLoaderTypeException(typeName);
        }
        return STRING_TYPE_NAME_TO_FACTORY_TYPE.get(typeName);
    }

}
