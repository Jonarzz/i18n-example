package io.github.czerepko.i18n.file;

class MissingTranslationResourcesException extends RuntimeException {

    private static final String ERROR_MESSAGE_TEMPLATE =
            "Missing translations resources."
            + " Please, make sure that subdirectory with name '%s' containing translation files exists in the 'i18n' resources directory";

    MissingTranslationResourcesException(String languageCode) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, languageCode));
    }

}
