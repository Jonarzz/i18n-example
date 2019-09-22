package io.github.czerepko.i18n.file;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

class YamlTranslationsLoader implements I18nTranslationsLoader {

    private static final String BEGINNING_WITH_LETTER_REGEX = "^[a-zA-Z].*";

    private I18nResourcesFinder i18nResourcesFinder;

    YamlTranslationsLoader() {
        i18nResourcesFinder = new I18nResourcesFinder("yml");
    }

    @Override
    public Map<String, String> loadTranslations(String languageCode) {
        Map<String, String> translations = new HashMap<>();
        for (File translationFile : i18nResourcesFinder.findI18nResources(languageCode)) {
            Stack<String> nodes = new Stack<>();
            int previousIndent = -1;
            int currentIndent;
            for (String line : FileUtils.getLines(translationFile)) {
                currentIndent = line.length() - line.replaceFirst(" *", "").length();
                if (line.contains("'") || line.contains("\"")) {
                    List<String> keyEndingAndValueList = Splitter.on(":").trimResults().splitToList(line);
                    String keyEnding;
                    if (keyEndingAndValueList.size() != 2 || !(keyEnding = keyEndingAndValueList.get(0)).matches(BEGINNING_WITH_LETTER_REGEX)) {
                        throw new InvalidTranslationsFileFormatException(languageCode, translationFile.getName());
                    }
                    nodes.add(keyEnding);
                    String key = Joiner.on(".").join(nodes);
                    nodes.pop();
                    if (translations.containsKey(key)) {
                        throw new DuplicatedTranslationKeyException(languageCode, translationFile.getName(), key);
                    }
                    String value = keyEndingAndValueList.get(1).replaceAll("['\"]", "");
                    translations.put(key, value);
                } else {
                    if (currentIndent <= previousIndent) {
                        nodes.pop();
                    }
                    nodes.add(line.replaceAll("[: ]", ""));
                }
                previousIndent = currentIndent;
            }
        }
        return translations;
    }

}
