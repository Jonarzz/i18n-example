package io.github.czerepko.i18n.dictionary;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Note - not a proper YAML format parser - used only as an example.
 */
class YamlTranslationsLoader implements I18nTranslationsLoader {

    private static final String BEGINNING_WITH_LETTER_REGEX = "^[a-zA-Z].*";

    private I18nResourcesFinder i18nResourcesFinder;

    YamlTranslationsLoader() {
        i18nResourcesFinder = new I18nResourcesFinder("yml");
    }

    @Override
    public Map<String, String> loadTranslations(String languageCode) {
        Map<String, String> translations = new HashMap<>();
        for (var fileContext : i18nResourcesFinder.findI18nResources(languageCode)) {
            Deque<String> nodes = new ArrayDeque<>();
            int previousIndent = -1;
            for (String line : fileContext.getLines()) {
                int currentIndent = line.length() - line.replaceFirst(" *", "").length();
                if (line.contains("'") || line.contains("\"")) {
                    StringTokenizer lineTokenizer = new StringTokenizer(line, ":");
                    String keyEnding;
                    if (lineTokenizer.countTokens() != 2 || !(keyEnding = lineTokenizer.nextToken().strip()).matches(BEGINNING_WITH_LETTER_REGEX)) {
                        throw new InvalidTranslationsFileFormatException(fileContext);
                    }
                    String key = String.join(".", nodes) + "." + keyEnding;
                    if (translations.containsKey(key)) {
                        throw new DuplicatedTranslationKeyException(fileContext, key);
                    }
                    translations.put(key, lineTokenizer.nextToken()
                                                       .replaceAll("(^[ '\"]+)|([ '\"]+$)", ""));
                } else {
                    if (currentIndent <= previousIndent) {
                        nodes.pollLast();
                    }
                    nodes.add(line.replaceAll("[: ]", ""));
                }
                previousIndent = currentIndent;
            }
        }
        return translations;
    }

}
