package io.github.czerepko.i18n.file;

import com.google.common.base.Splitter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PropertiesTranslationsLoader implements I18nTranslationsLoader {

    private static final String KEY_VALUE_SEPARATOR = "=";

    private I18nResourcesFinder i18nResourcesFinder;

    PropertiesTranslationsLoader() {
        i18nResourcesFinder = new I18nResourcesFinder("properties");
    }

    @Override
    public Map<String, String> loadTranslations(String languageCode) {
        Map<String, String> translations = new HashMap<>();
        for (File translationFile : i18nResourcesFinder.findI18nResources(languageCode)) {
            for (String line : FileUtils.getLines(translationFile)) {
                List<String> keyValueList = Splitter.on(KEY_VALUE_SEPARATOR).trimResults().splitToList(line);
                if (keyValueList.size() != 2) {
                    throw new InvalidTranslationsFileFormatException(languageCode, translationFile.getName());
                }
                String key = keyValueList.get(0);
                if (translations.containsKey(key)) {
                    throw new DuplicatedTranslationKeyException(languageCode, translationFile.getName(), key);
                }
                translations.put(key, keyValueList.get(1));
            }
        }
        return translations;
    }

}
