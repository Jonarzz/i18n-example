package io.github.czerepko.i18n.file;

import java.util.Set;

class InvalidTranslationsLoaderTypeException extends RuntimeException {

    private static final String ERROR_MESSAGE_TEMPLATE = "Translation loader does not exist for given type '%s', available types are: %s";

    InvalidTranslationsLoaderTypeException(String typeName, Set<String> availableTypes) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, typeName, availableTypes));
    }

}
