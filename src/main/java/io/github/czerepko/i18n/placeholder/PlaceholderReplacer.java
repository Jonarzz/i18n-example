package io.github.czerepko.i18n.placeholder;

import java.util.Map;

public class PlaceholderReplacer {

    private Map<String, String> placeholdersToValues;
    private PlaceholderDecoratingStrategy placeholderDecoratingStrategy;

    PlaceholderReplacer(Map<String, String> placeholdersToValues, PlaceholderDecoratingStrategy placeholderDecoratingStrategy) {
        this.placeholdersToValues = placeholdersToValues;
        this.placeholderDecoratingStrategy = placeholderDecoratingStrategy;
    }

    public String replace(String withPlaceholders) {
        String replaced = withPlaceholders;
        for (Map.Entry<String, String> placeholderToValue : placeholdersToValues.entrySet()) {
            String decoratedPlaceholder = placeholderDecoratingStrategy.apply(placeholderToValue.getKey());
            replaced = replaced.replace(decoratedPlaceholder, placeholderToValue.getValue());
        }
        return replaced;
    }

}
