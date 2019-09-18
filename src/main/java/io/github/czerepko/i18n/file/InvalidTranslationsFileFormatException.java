package io.github.czerepko.i18n.file;

class InvalidTranslationsFileFormatException extends RuntimeException {

    private static final String ERROR_MESSAGE_TEMPLATE = "File %s/%s has invalid format";

    InvalidTranslationsFileFormatException(String languageCode, String fileName) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, languageCode, fileName));
    }

}
