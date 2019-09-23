package io.github.czerepko.i18n.placeholder;

import java.util.Set;

class InvalidPlaceholderReplacerTypeException extends RuntimeException {

    private static final String ERROR_MESSAGE_TEMPLATE = "Placeholder replacer does not exist for given type '%s', available types are: %s";

    InvalidPlaceholderReplacerTypeException(String typeName, Set<String> availableTypes) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, typeName, availableTypes));
    }

}
