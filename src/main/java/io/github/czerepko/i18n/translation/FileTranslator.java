package io.github.czerepko.i18n.translation;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import io.github.czerepko.i18n.common.I18nProperties;
import io.github.czerepko.i18n.dictionary.I18nTranslationsLoader;
import io.github.czerepko.i18n.dictionary.TranslationsLoaderProvider;
import io.github.czerepko.i18n.file.FileHandler;
import io.github.czerepko.i18n.placeholder.PlaceholderReplacerFactory;

import java.io.File;
import java.util.List;

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
        List<String> filePathParts = Splitter.on(File.separator).splitToList(filePath);
        int lastElementIndex = filePathParts.size() - 1;

        String directoryPath = Joiner.on(File.separator).join(filePathParts.subList(0, lastElementIndex));

        String inputFileName = filePathParts.get(lastElementIndex);
        String outputFileName = inputFileName.replaceFirst("^(.+)\\.(.+)$", "$1-" + languageCode + ".$2");

        return new File(directoryPath + File.separator + outputFileName);
    }

}
