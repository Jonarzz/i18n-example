package io.github.czerepko.i18n.placeholder;

import io.github.czerepko.i18n.common.InvalidEnumTypeException;

import java.util.Collection;

class InvalidPlaceholderReplacerTypeException extends InvalidEnumTypeException {

    private static final String REPLACER_DESCRIPTION = "placeholder replacer";

    InvalidPlaceholderReplacerTypeException(String typeName, Collection<String> availableTypes) {
        super(REPLACER_DESCRIPTION, typeName, availableTypes);
    }

}
