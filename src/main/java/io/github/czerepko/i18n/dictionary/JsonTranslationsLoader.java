package io.github.czerepko.i18n.dictionary;

import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.eclipsesource.json.ParseException;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.flattener.KeyTransformer;

class JsonTranslationsLoader implements I18nTranslationsLoader {

    private I18nResourcesFinder i18nResourcesFinder;

    JsonTranslationsLoader() {
        i18nResourcesFinder = new I18nResourcesFinder("json");
    }

    @Override
    public Map<String, String> loadTranslations(String languageCode) {
        var translations = new HashMap<String, String>();
        for (var fileContext : i18nResourcesFinder.findI18nResources(languageCode)) {
            translations.putAll(getTranslationsFromFile(fileContext, translations));
        }
        return translations;
    }

    private static Map<String, String> getTranslationsFromFile(TranslationFileContext fileContext,
                                                               Map<String, String> loadedTranslations) {
        var alreadyOccurredKeys = new HashSet<>(loadedTranslations.keySet());
        var keyTransformer = new IncrementalKeyTransformer();
        var translationsWithNumberedKeys =
                createJsonFlattener(fileContext)
                        .withKeyTransformer(keyTransformer)
                        .flattenAsMap()
                        .entrySet()
                        .stream()
                        .collect(toMap(Map.Entry::getKey,
                                       entry -> entry.getValue().toString()));
        var translations = new HashMap<String, String>();
        translationsWithNumberedKeys.forEach((numberedKey, value) -> {
            // keys are numbered only to make it possible to catch duplicates and throw an error with appropriate information
            String key = keyTransformer.transformBack(numberedKey);
            if (alreadyOccurredKeys.contains(key)) {
                throw new DuplicatedTranslationKeyException(fileContext, key);
            }
            alreadyOccurredKeys.add(key);
            translations.put(key, value);
        });
        return translations;
    }

    private static JsonFlattener createJsonFlattener(TranslationFileContext fileContext) {
        try {
            return new JsonFlattener(fileContext.getContent());
        } catch (ParseException e) {
            throw new InvalidTranslationsFileFormatException(fileContext);
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
