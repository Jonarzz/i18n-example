package io.github.czerepko.i18n.dictionary;

import io.github.czerepko.i18n.common.InvalidEnumTypeException;

import java.util.Collection;

class InvalidTranslationsLoaderTypeException extends InvalidEnumTypeException {

    private static final String LOADER_DESCRIPTION = "translation loader";

    InvalidTranslationsLoaderTypeException(String typeName, Collection<String> availableTypes) {
        super(LOADER_DESCRIPTION, typeName, availableTypes);
    }

}
