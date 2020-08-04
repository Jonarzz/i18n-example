package io.github.czerepko.i18n.dictionary;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

class I18nResourcesFinder {

    private static final String I18N_PATH = "i18n/";

    private FilenameFilter filenameFilter;

    I18nResourcesFinder(String extension) {
        filenameFilter = (directory, filename) -> filename.matches(".+\\." + extension + "$");
    }

    Collection<TranslationFileContext> findI18nResources(String languageCode) {
        URL directoryUrl;
        try {
            directoryUrl = Optional.ofNullable(getClass().getClassLoader().getResource(I18N_PATH + languageCode))
                                   .orElseThrow(() -> new MissingTranslationResourcesException(languageCode));
        } catch (IllegalArgumentException e) {
            throw new MissingTranslationResourcesException(languageCode);
        }
        File[] translationFiles = new File(directoryUrl.getFile()).listFiles(filenameFilter);
        if (translationFiles == null || translationFiles.length == 0) {
            throw new MissingTranslationResourcesException(languageCode);
        }
        return Arrays.stream(translationFiles)
                     .map(TranslationFileContext::new)
                     .collect(toList());
    }

}
