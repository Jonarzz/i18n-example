package io.github.czerepko.i18n.dictionary;

class MissingTranslationResourcesException extends RuntimeException {

    private static final String ERROR_MESSAGE_TEMPLATE =
            "Missing translations resources."
            + " Please, make sure that subdirectory with name '%s' containing translation files exists in the 'i18n' resources directory."
            + " The files should have extensions appropriate for file format defined in the properties file.";

    MissingTranslationResourcesException(String languageCode) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, languageCode));
    }

}
