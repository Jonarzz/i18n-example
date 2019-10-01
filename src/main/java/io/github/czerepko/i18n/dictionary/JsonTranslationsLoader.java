package io.github.czerepko.i18n.dictionary;

import static java.util.stream.Collectors.toMap;

import com.eclipsesource.json.ParseException;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.flattener.KeyTransformer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class JsonTranslationsLoader implements I18nTranslationsLoader {

    private I18nResourcesFinder i18nResourcesFinder;

    JsonTranslationsLoader() {
        i18nResourcesFinder = new I18nResourcesFinder("json");
    }

    @Override
    public Map<String, String> loadTranslations(String languageCode) {
        Map<String, String> translations = new HashMap<>();
        for (TranslationFileContext fileContext : i18nResourcesFinder.findI18nResources(languageCode)) {
            String fileContent = fileContext.getContent();
            JsonFlattener jsonFlattener;
            try {
                jsonFlattener = new JsonFlattener(fileContent);
            } catch (ParseException e) {
                throw new InvalidTranslationsFileFormatException(fileContext);
            }
            Map<String, String> translationsFromFile = jsonFlattener.flattenAsMap()
                                                                    .entrySet()
                                                                    .stream()
                                                                    .collect(toMap(Map.Entry::getKey,
                                                                                   entry -> entry.getValue().toString()));
            validateDuplicatesInCurrentFile(fileContext, jsonFlattener, translationsFromFile);
            for (String key : translationsFromFile.keySet()) {
                if (translations.containsKey(key)) {
                    throw new DuplicatedTranslationKeyException(fileContext, key);
                }
                translations.put(key, translationsFromFile.get(key));
            }
        }
        return translations;
    }

    private void validateDuplicatesInCurrentFile(TranslationFileContext fileContext, JsonFlattener jsonFlattener,
                                                 Map<String, String> translationsFromFile) {
        IncrementalKeyTransformer keyTransformer = new IncrementalKeyTransformer();
        Set<String> numberedKeys = jsonFlattener.withKeyTransformer(keyTransformer)
                                                .flattenAsMap()
                                                .keySet();
        if (numberedKeys.size() == translationsFromFile.keySet().size()) {
            return;
        }
        Set<String> alreadyOccurredKeys = new HashSet<>();
        for (String numberedKey : numberedKeys) {
            String key = keyTransformer.transformBack(numberedKey);
            if (translationsFromFile.containsKey(key)) {
                if (alreadyOccurredKeys.contains(key)) {
                    throw new DuplicatedTranslationKeyException(fileContext, key);
                }
                alreadyOccurredKeys.add(key);
            }
        }
    }

    private static class IncrementalKeyTransformer implements KeyTransformer {

        private static final String KEY_TRANSFORMER_SEPARATOR = "'";

        private int index = 0;

        @Override
        public String transform(String key) {
            return KEY_TRANSFORMER_SEPARATOR + (index++) + KEY_TRANSFORMER_SEPARATOR + key;
        }

        private String transformBack(String transformedKey) {
            return transformedKey.replaceAll(KEY_TRANSFORMER_SEPARATOR + "\\d+" + KEY_TRANSFORMER_SEPARATOR, "");
        }

    }

}
