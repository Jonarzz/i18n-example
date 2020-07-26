package io.github.czerepko.i18n.translation;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@DisplayName("File translation executor tests")
class FileTranslationExecutorTest {

    private URL fileDirResourceUrl = getClass().getClassLoader().getResource("file/input");

    @ParameterizedTest(name = "type = {0}")
    @MethodSource("createFromStringProvider")
    @DisplayName("Create executor from string")
    void createFromStringTest(String type, FileTranslationExecutor expectedExecutor) {
        FileTranslationExecutor executor = FileTranslationExecutor.fromString(type);

        assertEquals(expectedExecutor, executor);
    }

    static Stream<Arguments> createFromStringProvider() {
        return Stream.of(
                Arguments.of("IMPLICIT",  FileTranslationExecutor.IMPLICIT),
                Arguments.of("implicit",  FileTranslationExecutor.IMPLICIT),
                Arguments.of("recursive", FileTranslationExecutor.RECURSIVE),
                Arguments.of("RECURSIVE", FileTranslationExecutor.RECURSIVE)
        );
    }

    @Test
    @DisplayName("Try to create executor from invalid string and get exception")
    void tryToCreateFromInvalidStringTest() {
        Exception exception = assertThrows(InvalidFileTranslationExecutorException.class,
                                           () -> FileTranslationExecutor.fromString("invalid"));
        assertEquals("File translation executor does not exist for given type 'invalid', available types are: [implicit, recursive]",
                     exception.getMessage());
    }

    @ParameterizedTest(name = "propertiesFileFormat = {0}, placeholderType = {1}")
    @MethodSource("createImplicitlyTranslatedFilesProvider")
    @DisplayName("Create translated files for implicitly given input file paths")
    void createImplicitlyTranslatedFiles(String propertiesFileFormat, String placeholderType, List<String> inputFileNames,
                                         Map<String, String> expectedFileNamesToContents) throws IOException, URISyntaxException {
        preparePropertiesFile(propertiesFileFormat, placeholderType);
        FileTranslationExecutor.reloadProperties();

        String[] inputFilePaths = inputFileNames.stream()
                                                .map(fileName -> Joiner.on(File.separator)
                                                                       .join(fileDirResourceUrl.getPath(), placeholderType, fileName))
                                                .toArray(String[]::new);

        List<String> translatedFilePaths = FileTranslationExecutor.IMPLICIT.createTranslatedFiles(inputFilePaths);

        assertEquals(expectedFileNamesToContents.size(), translatedFilePaths.size(),
                     "Number of translated file paths and expected number of file paths has to be equal");

        Map<String, String> actualFileNamesToContents =
                translatedFilePaths.stream()
                                   .collect(toMap(path -> Iterables.getLast(Splitter.on(File.separator)
                                                                                    .split(path)),
                                                  path -> {
                                                      try {
                                                          return Files.readString(Paths.get(path));
                                                      } catch (IOException e) {
                                                          throw new AssertionError(e);
                                                      }
                                                  }));
        for (String fileName : expectedFileNamesToContents.keySet()) {
            String expectedContent = expectedFileNamesToContents.get(fileName);
            String actualContent = actualFileNamesToContents.get(fileName);
            assertEquals(expectedContent, actualContent);
        }
    }

    static Stream<Arguments> createImplicitlyTranslatedFilesProvider() {
        List<String> inputFileNames = List.of("input-sentence-short.txt", "input-sentence-long.txt");
        Map<String, String> expectedFileNamesToContents = Map.of(
                "input-sentence-short-eng.txt", wrapTranslatedText("This is a short sentence"),
                "input-sentence-short-pol.txt", wrapTranslatedText("To jest krótkie zdanie"),
                "input-sentence-long-eng.txt", wrapTranslatedText("This is a pretty long sentence, which is used in the test"),
                "input-sentence-long-pol.txt", wrapTranslatedText("To jest dosyć długie zdanie, które jest wykorzystywane w teście")
        );
        return Stream.of(
                Arguments.of("properties", "variable", inputFileNames, expectedFileNamesToContents),
                Arguments.of("properties", "tag",      inputFileNames, expectedFileNamesToContents),
                Arguments.of("property",   "variable", inputFileNames, expectedFileNamesToContents),
                Arguments.of("property",   "tag",      inputFileNames, expectedFileNamesToContents),
                Arguments.of("json",       "variable", inputFileNames, expectedFileNamesToContents),
                Arguments.of("json",       "tag",      inputFileNames, expectedFileNamesToContents),
                Arguments.of("yaml",       "variable", inputFileNames, expectedFileNamesToContents),
                Arguments.of("yaml",       "tag",      inputFileNames, expectedFileNamesToContents),
                Arguments.of("yml",        "variable", inputFileNames, expectedFileNamesToContents),
                Arguments.of("yml",        "tag",      inputFileNames, expectedFileNamesToContents)
        );
    }

    static String wrapTranslatedText(String translatedText) {
        return "TEST " + translatedText + " TEST";
    }

    @SuppressWarnings("UnstableApiUsage")
    static void preparePropertiesFile(String fileFormat, String placeholderType) throws IOException, URISyntaxException {
        URI filePath = Resources.getResource("application.properties").toURI();
        List<String> propertiesFileContent = getPropertiesFileContent(fileFormat, placeholderType);
        Files.write(Paths.get(filePath), propertiesFileContent);
    }

    static List<String> getPropertiesFileContent(String fileFormat, String placeholderType) {
        return List.of("i18n.file.format      = " + fileFormat,
                       "i18n.placeholder.type = " + placeholderType,
                       "i18n.language.codes   = eng,pol");
    }

    @Test
    @DisplayName("Create translated files for given input file paths recursively")
    void createRecursivelyTranslatedFiles() {
        assertThrows(UnsupportedOperationException.class,
                     () -> FileTranslationExecutor.RECURSIVE.createTranslatedFiles(""));
    }

    @ParameterizedTest(name = "executor = {0}, paths = {1}")
    @MethodSource("translateWithNullOrEmptyPathsProvider")
    @DisplayName("Try to create translated files with null or empty input file paths")
    void tryToCreateTranslatedFilesWithNullOrEmptyPaths(FileTranslationExecutor executor, String[] paths) {
        Exception exception = assertThrows(IllegalArgumentException.class,
                                           () -> executor.createTranslatedFiles(paths));
        assertEquals("File paths cannot be null or empty", exception.getMessage());
    }

    static Stream<Arguments> translateWithNullOrEmptyPathsProvider() {
        return Stream.of(
                Arguments.of(FileTranslationExecutor.IMPLICIT,  null),
                Arguments.of(FileTranslationExecutor.IMPLICIT,  new String[0]),
                Arguments.of(FileTranslationExecutor.RECURSIVE, null),
                Arguments.of(FileTranslationExecutor.RECURSIVE, new String[0])
        );
    }

}