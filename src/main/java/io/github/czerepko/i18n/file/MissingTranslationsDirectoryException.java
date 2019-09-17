package io.github.czerepko.i18n.file;

class MissingTranslationsDirectoryException extends RuntimeException {

    private static final String ERROR_MESSAGE_TEMPLATE =
            "Missing translations directory. Please, make sure that subdirectory with name '%s' exists in the 'i18n' resources directory";

    MissingTranslationsDirectoryException(String languageCode) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, languageCode));
    }

}
