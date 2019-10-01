package io.github.czerepko.i18n.dictionary;

class InvalidTranslationsFileFormatException extends RuntimeException {

    private static final String ERROR_MESSAGE_TEMPLATE = "File %s has invalid format";

    InvalidTranslationsFileFormatException(TranslationFileContext fileContext) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, fileContext.getPath()));
    }

}
