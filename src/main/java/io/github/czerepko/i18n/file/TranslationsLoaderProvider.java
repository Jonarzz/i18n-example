package io.github.czerepko.i18n.file;

import java.util.Map;

public enum TranslationsLoaderProvider {
    JSON(new JsonTranslationsLoader()),
    YAML(new YamlTranslationsLoader()),
    PROPERTIES(new PropertiesTranslationsLoader());

    private static final Map<String, TranslationsLoaderProvider> STRING_TYPE_NAME_TO_PROVIDER_TYPE = Map.of(
            "json",       JSON,
            "yaml",       YAML,
            "yml",        YAML,
            "properties", PROPERTIES,
            "property",   PROPERTIES
    );

    private I18nTranslationsLoader translationsLoader;

    TranslationsLoaderProvider(I18nTranslationsLoader translationsLoader) {
        this.translationsLoader = translationsLoader;
    }

    public I18nTranslationsLoader get() {
        return translationsLoader;
    }

    /**
     * @param typeName translations loader type name for which the provider should be returned
     * @return The provider appropriate for given type name
     * @throws InvalidTranslationsLoaderTypeException when given type name is not valid; the exception message contains all available type names
     */
    public static TranslationsLoaderProvider fromString(String typeName) throws InvalidTranslationsLoaderTypeException {
        if (!STRING_TYPE_NAME_TO_PROVIDER_TYPE.containsKey(typeName)) {
            throw new InvalidTranslationsLoaderTypeException(typeName, STRING_TYPE_NAME_TO_PROVIDER_TYPE.keySet());
        }
        return STRING_TYPE_NAME_TO_PROVIDER_TYPE.get(typeName);
    }

}
