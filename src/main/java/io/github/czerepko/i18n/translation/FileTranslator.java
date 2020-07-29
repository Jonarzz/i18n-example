package io.github.czerepko.i18n.translation;

import io.github.czerepko.i18n.common.I18nProperties;
import io.github.czerepko.i18n.dictionary.I18nTranslationsLoader;
import io.github.czerepko.i18n.dictionary.TranslationsLoaderProvider;
import io.github.czerepko.i18n.file.FileHandler;
import io.github.czerepko.i18n.placeholder.PlaceholderReplacerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

class FileTranslator {

    private I18nTranslationsLoader i18nTranslationsLoader;
    private PlaceholderReplacerFactory placeholderReplacerFactory;

    FileTranslator() {
        i18nTranslationsLoader = TranslationsLoaderProvider.fromString(I18nProperties.FILE_FORMAT.getValue()).get();
        placeholderReplacerFactory = PlaceholderReplacerFactory.fromString(I18nProperties.PLACEHOLDER_TYPE.getValue());
    }

    File translate(File templateFile, String languageCode) {
        String input = FileHandler.READ.execute(templateFile);
        var translations = i18nTranslationsLoader.loadTranslations(languageCode);
        String output = placeholderReplacerFactory.create(translations)
                                                  .replace(input);
        File outputFile = getOutputFile(languageCode, templateFile.getPath());
        FileHandler.CREATE.execute(outputFile, output);
        return outputFile;
    }

    private File getOutputFile(String languageCode, String filePath) {
        Path path = Path.of(filePath);
        String outputFileName = Optional.ofNullable(path.getFileName())
                                        .map(Path::toString)
                                        .map(fileName -> fileName.replaceFirst("^(.+)\\.(.+)$", "$1-" + languageCode + ".$2"))
                                        .orElseThrow(() -> new IllegalArgumentException("File path " + filePath + " is invalid - no file name present"));
        return new File(path.getParent() + File.separator + outputFileName);
    }

}
