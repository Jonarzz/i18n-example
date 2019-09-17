package io.github.czerepko.i18n.file;

import java.util.Map;

public interface I18nTranslationsLoader {

    /**
     * @param languageCode ISO 639-2 language code as String
     * @return Map of translations as {placeholder : translation in given language} pairs.
     */
    Map<String, String> loadTranslations(String languageCode);

}
