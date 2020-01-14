package io.github.jonarzz;

import static io.github.jonarzz.test.TestConstants.ENGLISH_DICTIONARY_FILE_CONTENT_LINES;
import static io.github.jonarzz.test.TestConstants.ENGLISH_LANGUAGE;
import static io.github.jonarzz.test.TestConstants.LANGUAGE_NAMES;
import static io.github.jonarzz.test.TestConstants.POLISH_DICTIONARY_FILE_CONTENT_LINES;
import static io.github.jonarzz.test.TestConstants.POLISH_LANGUAGE;
import static io.github.jonarzz.test.TestConstants.TEMPLATE_1_FILE_CONTENT_LINES;
import static io.github.jonarzz.test.TestConstants.TEMPLATE_1_FILE_NAME;
import static io.github.jonarzz.test.TestConstants.TEMPLATE_2_FILE_CONTENT_LINES;
import static io.github.jonarzz.test.TestConstants.TEMPLATE_2_FILE_NAME;
import static io.github.jonarzz.test.TestConstants.TEMPLATE_FILE_NAMES;
import static io.github.jonarzz.test.TestConstants.TRANSLATED_FILENAME_TO_EXPECTED_CONTENT_LINES;
import static io.github.jonarzz.test.TestConstants.propertiesFileName;
import static io.github.jonarzz.test.TestConstants.translatedFileName;
import static io.github.jonarzz.test.TestResourceUtils.createResource;
import static io.github.jonarzz.test.TestResourceUtils.createResourceDirectory;
import static io.github.jonarzz.test.TestResourceUtils.getResource;
import static io.github.jonarzz.test.TestResourceUtils.getResourceFileForAssertion;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@DisplayName("Internationalization tool acceptance tests")
class I18nExampleTest {

    private static final String RESOURCE_SUBDIRECTORY_NAME = "acceptance";
    private static final String DICTIONARY_SUBDIRECTORY_NAME = RESOURCE_SUBDIRECTORY_NAME + "/dictionary";
    private static final String OUTPUT_SUBDIRECTORY_NAME     = RESOURCE_SUBDIRECTORY_NAME + "/output";
    private static final String TEMPLATE_SUBDIRECTORY_NAME   = RESOURCE_SUBDIRECTORY_NAME + "/template";

    @BeforeAll
    static void setup() throws URISyntaxException, IOException {
        createResourceDirectory(RESOURCE_SUBDIRECTORY_NAME);

        File templateDirectory = createResourceDirectory(TEMPLATE_SUBDIRECTORY_NAME);
        createResource(templateDirectory, TEMPLATE_1_FILE_NAME, TEMPLATE_1_FILE_CONTENT_LINES);
        createResource(templateDirectory, TEMPLATE_2_FILE_NAME, TEMPLATE_2_FILE_CONTENT_LINES);

        File dictionaryDirectory = createResourceDirectory(DICTIONARY_SUBDIRECTORY_NAME);
        createResource(dictionaryDirectory, propertiesFileName(ENGLISH_LANGUAGE), ENGLISH_DICTIONARY_FILE_CONTENT_LINES);
        createResource(dictionaryDirectory, propertiesFileName(POLISH_LANGUAGE),  POLISH_DICTIONARY_FILE_CONTENT_LINES);

        createResourceDirectory(OUTPUT_SUBDIRECTORY_NAME);
    }

    @Test
    @DisplayName("Successfully create translation files")
    void successfullyCreateTranslationFiles() {
        String dictionaryDirectory = getResource(DICTIONARY_SUBDIRECTORY_NAME, "").getPath();
        String outputDirectory = getResource(OUTPUT_SUBDIRECTORY_NAME, "").getPath();
        Stream<String> templateFilePathsStream =
                TEMPLATE_FILE_NAMES.stream()
                                   .map(templateName -> getResource(TEMPLATE_SUBDIRECTORY_NAME,
                                                                    templateName))
                                   .map(URL::getPath);
        String[] arguments = Stream.concat(
                Stream.of(dictionaryDirectory, outputDirectory),
                templateFilePathsStream
        ).toArray(String[]::new);

        I18nExample.main(arguments);

        assertAll(
                getTranslatedFilesStreamForAssertion().map(either -> () -> {
                    File file = either.getOrElseThrow(either::getLeft);
                    assertTranslatedFile(file);
                })
        );
    }

    private Stream<Either<AssertionFailedError, File>> getTranslatedFilesStreamForAssertion() {
        Stream.Builder<Either<AssertionFailedError, File>> streamBuilder = Stream.builder();
        for (String templateFileName : TEMPLATE_FILE_NAMES) {
            for (String languageName : LANGUAGE_NAMES) {
                String translatedFileName = translatedFileName(templateFileName, languageName);
                streamBuilder.add(getResourceFileForAssertion(OUTPUT_SUBDIRECTORY_NAME, translatedFileName));
            }
        }
        return streamBuilder.build();
    }

    private void assertTranslatedFile(File file) throws IOException {
        assertTrue(file.exists(),
                   "Translated file " + file.getName() + " does not exist");

        String fileName = file.getName();
        assertTrue(TRANSLATED_FILENAME_TO_EXPECTED_CONTENT_LINES.containsKey(fileName),
                   () -> "Translated file name " + file.getName()
                         + " is not one of " + TRANSLATED_FILENAME_TO_EXPECTED_CONTENT_LINES.keySet());

        Path filePath = Paths.get(file.getPath());
        List<String> expectedLines = TRANSLATED_FILENAME_TO_EXPECTED_CONTENT_LINES.get(fileName);
        List<String> actualLines = Files.readAllLines(filePath);
        assertEquals(expectedLines.size(), actualLines.size(),
                     () -> "Number of lines in file " + file.getName() + " is different than expected");
        for (int i = 0; i < expectedLines.size(); i++) {
            assertEquals(actualLines.get(i), actualLines.get(i),
                         "Line " + (i + 1) + " in file " + file.getName() + " is different than expected");
        }
    }

}