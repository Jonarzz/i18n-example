package io.github.czerepko.i18n.file;

import static java.util.stream.Collectors.toMap;

import com.eclipsesource.json.ParseException;
import com.github.wnameless.json.flattener.JsonFlattener;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

class JsonTranslationsLoader implements I18nTranslationsLoader {

    private static final String KEY_TRANSFORMER_SEPARATOR = "'";

    private I18nResourcesFinder i18nResourcesFinder;

    JsonTranslationsLoader() {
        i18nResourcesFinder = new I18nResourcesFinder("json");
    }

    @Override
    public Map<String, String> loadTranslations(String languageCode) {
        Map<String, String> translations = new TreeMap<>();
        File[] findI18nResources = i18nResourcesFinder.findI18nResources(languageCode);
        for (File translationFile : findI18nResources) {
            String fileContent = FileUtils.getContent(translationFile);
            JsonFlattener jsonFlattener;
            try {
                jsonFlattener = new JsonFlattener(fileContent);
            } catch (ParseException e) {
                throw new InvalidTranslationsFileFormatException(languageCode, translationFile.getName());
            }
            Map<String, String> translationsFromFile = jsonFlattener.flattenAsMap()
                                                                    .entrySet()
                                                                    .stream()
                                                                    .collect(toMap(Map.Entry::getKey,
                                                                                   entry -> entry.getValue().toString()));
            validateDuplicatesInCurrentFile(languageCode, translationFile, jsonFlattener, translationsFromFile);
            for (String key : translationsFromFile.keySet()) {
                if (translations.containsKey(key)) {
                    throw new DuplicatedTranslationKeyException(languageCode, translationFile.getName(), key);
                }
                translations.put(key, translationsFromFile.get(key));
            }
        }
        return translations;
    }

    private void validateDuplicatesInCurrentFile(String languageCode, File translationFile, JsonFlattener jsonFlattener,
                                                 Map<String, String> translationsFromFile) {
        IntegerHolder index = new IntegerHolder();
        Set<String> numberedKeys = jsonFlattener.withKeyTransformer(key -> KEY_TRANSFORMER_SEPARATOR + index.getAndIncrement()
                                                                           + KEY_TRANSFORMER_SEPARATOR + key)
                                                .flattenAsMap()
                                                .keySet();
        if (numberedKeys.size() == translationsFromFile.keySet().size()) {
            return;
        }
        Set<String> alreadyOccurredKeys = new HashSet<>();
        for (String numberedKey : numberedKeys) {
            String key = numberedKey.replaceAll(KEY_TRANSFORMER_SEPARATOR + "\\d+" + KEY_TRANSFORMER_SEPARATOR, "");
            if (translationsFromFile.containsKey(key)) {
                if (alreadyOccurredKeys.contains(key)) {
                    throw new DuplicatedTranslationKeyException(languageCode, translationFile.getName(), key);
                }
                alreadyOccurredKeys.add(key);
            }
        }
    }

    private static class IntegerHolder {
        private int integer = 0;

        private int getAndIncrement() {
            return integer++;
        }
    }

}
