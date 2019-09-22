package io.github.czerepko.i18n.file;

import com.google.common.io.Resources;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;

@SuppressWarnings("UnstableApiUsage")
class I18nResourcesFinder {

    private static final String I18N_PATH = "i18n/";

    private FilenameFilter filenameFilter;

    I18nResourcesFinder(String extension) {
        filenameFilter = (directory, filename) -> filename.matches(".+\\." + extension + "$");
    }

    File[] findI18nResources(String languageCode) {
        URL directoryUrl;
        try {
            directoryUrl = Resources.getResource(I18N_PATH + languageCode);
        } catch (IllegalArgumentException e) {
            throw new MissingTranslationResourcesException(languageCode);
        }
        File[] translationFiles = new File(directoryUrl.getFile()).listFiles(filenameFilter);
        if (translationFiles == null || translationFiles.length == 0) {
            throw new MissingTranslationResourcesException(languageCode);
        }
        return translationFiles;
    }

}
