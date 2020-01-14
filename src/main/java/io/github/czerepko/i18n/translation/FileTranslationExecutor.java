package io.github.czerepko.i18n.translation;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.google.common.base.Splitter;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
public enum FileTranslationExecutor {
    IMPLICIT(new ImplicitFileResolver()),
    RECURSIVE(new RecursiveFileResolver());

    private static final Map<String, FileTranslationExecutor> TYPE_TO_EXECUTOR = Arrays.stream(values())
                                                                                       .collect(toMap(value -> value.toString().toLowerCase(),
                                                                                                      identity()));
    private static final List<String> LANGUAGE_CODES = Splitter.on(",")
                                                               .trimResults()
                                                               .splitToList(TranslationProperties.LANGUAGE_CODES.getValue());

    private TemplateFileResolver templateFileResolver;

    FileTranslationExecutor(TemplateFileResolver templateFileResolver) {
        this.templateFileResolver = templateFileResolver;
    }

    public static FileTranslationExecutor fromString(String type) {
        String lowerCaseType = type.toLowerCase();
        if (!TYPE_TO_EXECUTOR.containsKey(lowerCaseType)) {
            throw new InvalidFileTranslationExecutorException(type, TYPE_TO_EXECUTOR.keySet());
        }
        return TYPE_TO_EXECUTOR.get(lowerCaseType);
    }

    public static void reloadProperties() {
        TranslationProperties.reload();
    }

    /**
     * Loads files (templates) from given paths and translates their contents with use of the translation dictionary resource files from the 'i18n'
     * language code subdirectories.
     * <br/><br/>
     * Translated content is saved to a file with language code appended to the filename,
     * e.g. if the input file is "some-file.txt" and the language codes are "eng" and "pol" (with their own translations
     * in the resources subdirectories), the output files will be: "some-file-eng.txt" for the English translations and "some-file-pol.txt"
     * for the Polish translations - all files are saved to directory of the source (template) file.
     * Returns list of translated file paths.
     *
     * @param filePaths paths to files that should undergo the translation process
     * @return List of translated file paths.
     */
    public List<String> createTranslatedFiles(String... filePaths) {
        if (filePaths == null || filePaths.length == 0) {
            throw new IllegalArgumentException("File paths cannot be null or empty");
        }
        return templateFileResolver.resolve(filePaths)
                                   .stream()
                                   .flatMap(templateFile -> LANGUAGE_CODES
                                           .stream()
                                           .map(languageCode -> new FileTranslator().translate(templateFile, languageCode)))
                                   .map(File::getPath)
                                   .collect(toList());
    }

}
