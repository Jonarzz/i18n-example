package io.github.czerepko.i18n.file;

class InvalidTranslationsLoaderTypeException extends RuntimeException {

    private static final String ERROR_MESSAGE_PREFIX = "Translation loader does not exist for given type: ";

    InvalidTranslationsLoaderTypeException(String typeName) {
        super(ERROR_MESSAGE_PREFIX + typeName);
    }

}
