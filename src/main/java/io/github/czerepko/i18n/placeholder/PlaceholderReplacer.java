package io.github.czerepko.i18n.placeholder;

import java.util.Map;

public class PlaceholderReplacer {

    private Map<String, String> placeholdersToValues;
    private PlaceholderDecoratingStrategy placeholderDecoratingStrategy;

    PlaceholderReplacer(Map<String, String> placeholdersToValues, PlaceholderDecoratingStrategy placeholderDecoratingStrategy) {
        this.placeholdersToValues = placeholdersToValues;
        this.placeholderDecoratingStrategy = placeholderDecoratingStrategy;
    }

    /**
     * Takes a String with placeholders and replaces them with appropriate values.
     * Both replacement mapping and placeholder decoration strategy (wrapping keys to match appropriate placeholder style)
     * are defined at the object creation.
     *
     * @param withPlaceholders String that contains parts with placeholders in format appropriate for this replacer
     *                         (see: {@link io.github.czerepko.i18n.placeholder.PlaceholderReplacerFactory})
     * @return A new String with all placeholders from the given String replaced with appropriate values based on the placeholder to value map.
     */
    public String replace(String withPlaceholders) {
        String replaced = withPlaceholders;
        for (Map.Entry<String, String> placeholderToValue : placeholdersToValues.entrySet()) {
            String decoratedPlaceholder = placeholderDecoratingStrategy.apply(placeholderToValue.getKey());
            replaced = replaced.replace(decoratedPlaceholder, placeholderToValue.getValue());
        }
        return replaced;
    }

}
