package io.github.czerepko.i18n.dictionary;

import static com.google.common.base.CharMatcher.anyOf;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        for (TranslationFileContext fileContext : i18nResourcesFinder.findI18nResources(languageCode)) {
            Deque<String> nodes = new ArrayDeque<>();
            int previousIndent = -1;
            for (String line : fileContext.getLines()) {
                int currentIndent = line.length() - line.replaceFirst(" *", "").length();
                if (line.contains("'") || line.contains("\"")) {
                    List<String> keyEndingAndValueList = Splitter.on(":").trimResults(anyOf(" '\"")).splitToList(line);
                    String keyEnding;
                    if (keyEndingAndValueList.size() != 2 || !(keyEnding = keyEndingAndValueList.get(0)).matches(BEGINNING_WITH_LETTER_REGEX)) {
                        throw new InvalidTranslationsFileFormatException(fileContext);
                    }
                    String key = Joiner.on(".").join(nodes) + "." + keyEnding;
                    if (translations.containsKey(key)) {
                        throw new DuplicatedTranslationKeyException(fileContext, key);
                    }
                    translations.put(key, keyEndingAndValueList.get(1));
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
