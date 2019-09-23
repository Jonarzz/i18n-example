package io.github.czerepko;

import io.github.czerepko.i18n.I18nTranslator;

import java.io.IOException;

public class I18nExample {

    public static void main(String[] args) throws IOException {
        I18nTranslator.translateFiles(args);
    }

}