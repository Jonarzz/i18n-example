package io.github.czerepko.i18n.file;

class DuplicatedTranslationKeyException extends RuntimeException {

    private static final String ERROR_MESSAGE_TEMPLATE = "File %s contains duplicated translation key: '%s'";

    DuplicatedTranslationKeyException(TranslationFileContext fileContext, String duplicatedKey) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, fileContext.getPath(), duplicatedKey));

    }

}
