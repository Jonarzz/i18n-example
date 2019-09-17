package io.github.czerepko.i18n.file;

import java.util.Map;

public interface I18nTranslationsLoader {

    /**
     * @param languageCode Language code as string - represents the translations source subdirectory in i18n resources directory
     * @return Map of translations as {placeholder : translation in given language} pairs.
     */
    Map<String, String> loadTranslations(String languageCode);

}
