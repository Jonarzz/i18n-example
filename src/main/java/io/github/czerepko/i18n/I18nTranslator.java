package io.github.czerepko.i18n;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.io.Resources;
import io.github.czerepko.i18n.file.TranslationsLoaderProvider;
import io.github.czerepko.i18n.placeholder.PlaceholderReplacerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Translator that allows to generate files with translations for multiple languages
 * based on a single template file and multiple language dictionaries.
 * <br/><br/>
 * Required properties loaded from <b>application.properties</b> resource file are:<br/>
 * <ul>
 *     <li><b>i18n.language.codes</b> - target languages to which the template should be translated, <b>separated by a comma</b>;<br/>
 *                                      have to reflect the 'i18n' resource translation subdirectories, e.g. for value: "eng,pol"
 *                                      resource directories "i18n/eng" and "i18n/pol" have to be present with translation dictionaries
 *                                      in appropriate format inside the directories</li>
 *     <li><b>i18n.file.format</b> - format of the translation files, should be one of:
 *         <ul>
 *             <li>json - JavaScript-object-based format</li>
 *             <li>yml - YAML format</li>
 *             <li>properties - key-value pairs separated by an equality sign</li>
 *         </ul>
 *     </li>
 *     <li><b>i18n.placeholder.type</b> - type of the placeholder used in the template files, should be one of:
 *         <ul>
 *             <li>tag - for {@code <Trans>place.holder.here</Trans>}</li>
 *             <li>variable - for {@code ${place.holder.here}}</li>
 *         </ul>
 *     </li>
 * </ul>
 */
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

    /**
     * Loads files (templates) from given paths and translates their contents with use of the translation dictionary resource files from the 'i18n'
     * language code subdirectories.
     * <br/><br/>
     * Translated content is saved to a file with language code appended to the filename,
     * e.g. if the input file is "some-file.txt" and the language codes are "eng" and "pol" (with their own translations
     * in the resources subdirectories), the output files will be: "some-file-eng.txt" for the English translations and "some-file-pol.txt"
     * for the Polish translations - all files are saved to directory of the source (template) file.
     *
     * @param filePaths paths to files that should undergo the translation process
     * @throws IOException when any input-output problem with reading from, creating or writing to a file occurs
     */
    public static void translateFiles(String... filePaths) throws IOException {
        if (filePaths == null) {
            return;
        }
        for (String inputFilePath : filePaths) {
            String input = Files.readString(Path.of(inputFilePath));
            for (String languageCode : Splitter.on(",").trimResults().split(LANGUAGE_CODES_PROPERTY)) {
                String output = translate(input, languageCode);
                File outputFile = getOutputFile(languageCode, inputFilePath);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                Files.writeString(outputFile.toPath(), output);
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
