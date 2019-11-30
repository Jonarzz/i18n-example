package io.github.czerepko;

import io.github.czerepko.i18n.translation.FileTranslationExecutor;

public class I18nExample {

    public static void main(String[] args) {
        FileTranslationExecutor.IMPLICIT
                .createTranslatedFiles(args)
                .forEach(translatedFilePath -> System.out.println("Created " + translatedFilePath));
    }

}