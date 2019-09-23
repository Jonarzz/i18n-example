package io.github.czerepko.i18n.file;

import static java.util.stream.Collectors.toList;

import com.google.common.io.Resources;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Arrays;

@SuppressWarnings("UnstableApiUsage")
class I18nResourcesFinder {

    private static final String I18N_PATH = "i18n/";

    private FilenameFilter filenameFilter;

    I18nResourcesFinder(String extension) {
        filenameFilter = (directory, filename) -> filename.matches(".+\\." + extension + "$");
    }

    Iterable<TranslationFileContext> findI18nResources(String languageCode) {
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
        return Arrays.stream(translationFiles)
                     .map(file -> new TranslationFileContext(languageCode, file))
                     .collect(toList());
    }

}
