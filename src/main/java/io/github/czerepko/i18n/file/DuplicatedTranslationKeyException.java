package io.github.czerepko.i18n.file;

class DuplicatedTranslationKeyException extends RuntimeException {

    private static final String ERROR_MESSAGE_TEMPLATE = "File '%s/%s' contains duplicated translation key: '%s'";

    DuplicatedTranslationKeyException(String languageCode, String fileName, String duplicatedKey) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, languageCode, fileName, duplicatedKey));
    }

}
