package io.github.czerepko.i18n.translation;

import io.github.czerepko.i18n.common.InvalidEnumTypeException;

import java.util.Collection;

class InvalidFileTranslationExecutorException extends InvalidEnumTypeException {

    private static final String EXECUTOR_DESCRIPTION = "file translation executor";

    InvalidFileTranslationExecutorException(String type, Collection<String> availableTypes) {
        super(EXECUTOR_DESCRIPTION, type, availableTypes);
    }

}
