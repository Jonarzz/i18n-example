package io.github.czerepko.i18n.placeholder;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;

public enum PlaceholderReplacerFactory {
    TAG     (placeholder -> "<Trans>" + placeholder + "</Trans>"),
    VARIABLE(placeholder -> "${" + placeholder + "}");

    private static final Map<String, PlaceholderReplacerFactory> STRING_TYPE_NAME_TO_FACTORY_TYPE =
            Arrays.stream(values()).collect(toMap(value -> value.toString().toLowerCase(),
                                                  identity()));

    private PlaceholderDecoratingStrategy placeholderDecoratingStrategy;

    PlaceholderReplacerFactory(PlaceholderDecoratingStrategy placeholderDecoratingStrategy) {
        this.placeholderDecoratingStrategy = placeholderDecoratingStrategy;
    }

    public PlaceholderReplacer create(Map<String, String> placeholdersToValues) {
        return new PlaceholderReplacer(placeholdersToValues, placeholderDecoratingStrategy);
    }

    /**
     * @param typeName placeholder replacer factory type name for which the factory should be returned
     * @return The replacer factory appropriate for given type name
     * @throws InvalidPlaceholderReplacerTypeException when given type name is not valid; the exception message contains all available type names
     */
    public static PlaceholderReplacerFactory fromString(String typeName) throws InvalidPlaceholderReplacerTypeException {
        if (!STRING_TYPE_NAME_TO_FACTORY_TYPE.containsKey(typeName)) {
            throw new InvalidPlaceholderReplacerTypeException(typeName, STRING_TYPE_NAME_TO_FACTORY_TYPE.keySet());
        }
        return STRING_TYPE_NAME_TO_FACTORY_TYPE.get(typeName);
    }

}