package io.github.czerepko;

import io.github.czerepko.i18n.translation.FileTranslationExecutor;

public class I18nExample {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("No arguments provided! Template file paths should be provided as arguments.");
            System.exit(1);
        }
        FileTranslationExecutor.IMPLICIT // TODO parameterize execution type
                .createTranslatedFiles(args)
                .forEach(translatedFilePath -> System.out.println("Created " + translatedFilePath));
    }

}