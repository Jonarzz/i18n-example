package io.github.czerepko.i18n.common;

import java.util.Collection;

public abstract class InvalidEnumTypeException extends RuntimeException {

    private static final String ERROR_MESSAGE_TEMPLATE = "%s does not exist for given type '%s', available types are: %s";

    protected InvalidEnumTypeException(String enumDescription, String invalidEnumType, Collection<String> availableTypes) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, capitalize(enumDescription), invalidEnumType, availableTypes));
    }

    private static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

}
