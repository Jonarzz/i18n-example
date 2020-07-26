package io.github.czerepko.i18n.dictionary;

import java.util.Map;

public interface I18nTranslationsLoader {

    /**
     * Creates translations map of placeholder - translation pairs for given language code.
     *
     * @param languageCode Language code as string - represents the translations source subdirectory in i18n resources directory
     * @return Map of translations as {placeholder : translation in given language} pairs.
     * @throws InvalidTranslationsFileFormatException when any file with extension appropriate for the loader in the language code subdirectory
     *                                                does not comply with the file format
     * @throws MissingTranslationResourcesException when the subdirectory for given language code does not exist in 'i18n' resources directory
     * @throws DuplicatedTranslationKeyException when any of the translation files in the subdirectory for given language contains a duplicate key
     */
    Map<String, String> loadTranslations(String languageCode)
            throws InvalidTranslationsFileFormatException, MissingTranslationResourcesException, DuplicatedTranslationKeyException;

}
