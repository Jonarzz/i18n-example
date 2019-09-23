package io.github.czerepko.i18n;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import io.github.czerepko.i18n.file.TranslationsLoaderProvider;
import io.github.czerepko.i18n.placeholder.PlaceholderReplacerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("UnstableApiUsage")
public class I18nTranslator {

    private static final String LANGUAGE_CODES_PROPERTY;
    private static final String FILE_FORMAT_PROPERTY;
    private static final String PLACEHOLDER_TYPE_PROPERTY;

    static {
        Properties properties = new Properties();
        try {
            properties.load(Resources.getResource("application.properties").openStream());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LANGUAGE_CODES_PROPERTY   = properties.getProperty("i18n.language.codes");
        FILE_FORMAT_PROPERTY      = properties.getProperty("i18n.file.format");
        PLACEHOLDER_TYPE_PROPERTY = properties.getProperty("i18n.placeholder.type");
    }

    public static void translateFiles(String... filePaths) throws IOException {
        for (String inputFilePath : filePaths) {
            String input = Files.asCharSource(new File(inputFilePath), Charsets.UTF_8).read();
            for (String languageCode : Splitter.on(",").trimResults().split(LANGUAGE_CODES_PROPERTY)) {
                String output = translate(input, languageCode);
                File outputFile = getOutputFile(languageCode, inputFilePath);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                Files.asCharSink(outputFile, Charsets.UTF_8).write(output);
            }
        }
    }

    private static String translate(String input, String languageCode) {
        Map<String, String> translations = TranslationsLoaderProvider.fromString(FILE_FORMAT_PROPERTY)
                                                                     .get()
                                                                     .loadTranslations(languageCode);
        return PlaceholderReplacerFactory.fromString(PLACEHOLDER_TYPE_PROPERTY)
                                         .create(translations)
                                         .replace(input);
    }

    private static File getOutputFile(String languageCode, String filePath) {
        List<String> filePathParts = Splitter.on(File.separator).splitToList(filePath);
        int lastElementIndex = filePathParts.size() - 1;

        String directoryPath = Joiner.on(File.separator).join(filePathParts.subList(0, lastElementIndex));

        String inputFileName = filePathParts.get(lastElementIndex);
        String outputFileName = inputFileName.replaceFirst("^(.+)\\.(.+)$", "$1-" + languageCode + ".$2");

        return new File(directoryPath + File.separator + outputFileName);
    }

}
