package io.github.czerepko.i18n.translation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@DisplayName("File translation executor tests")
class FileTranslationExecutorTest {

    @ParameterizedTest(name = "type = {0}")
    @MethodSource("createFromStringProvider")
    @DisplayName("Create executor from string")
    void createFromStringTest(String type, FileTranslationExecutor expectedExecutor) {
        FileTranslationExecutor executor = FileTranslationExecutor.fromString(type);

        assertEquals(expectedExecutor, executor);
    }

    private static Stream<Arguments> createFromStringProvider() {
        return Stream.of(
                Arguments.of("IMPLICIT",  FileTranslationExecutor.IMPLICIT),
                Arguments.of("implicit",  FileTranslationExecutor.IMPLICIT),
                Arguments.of("recursive", FileTranslationExecutor.RECURSIVE),
                Arguments.of("RECURSIVE", FileTranslationExecutor.RECURSIVE)
        );
    }

    @Test
    @DisplayName("Try reate executor from invalid string and get exception")
    void tryToCreateFromInvalidStringTest() {
        Exception exception = assertThrows(InvalidFileTranslationExecutorException.class,
                                           () -> FileTranslationExecutor.fromString("invalid"));
        assertEquals("File translation executor does not exist for given type 'invalid', available types are: [implicit, recursive]",
                     exception.getMessage());
    }

    @Test
    @DisplayName("Create translated files for given input file paths")
    void createTranslatedFiles() {
        // TODO
    }

    @ParameterizedTest(name = "executor = {0}, paths = {1}")
    @MethodSource("translateWithNullOrEmptyPathsProvider")
    @DisplayName("Try to create translated files with null or empty input file paths")
    void tryToCreateTranslatedFilesWithNullOrEmptyPaths(FileTranslationExecutor executor, String[] paths) {
        Exception exception = assertThrows(IllegalArgumentException.class,
                                           () -> executor.createTranslatedFiles(paths));
        assertEquals("File paths cannot be null or empty", exception.getMessage());
    }

    private static Stream<Arguments> translateWithNullOrEmptyPathsProvider() {
        return Stream.of(
                Arguments.of(FileTranslationExecutor.IMPLICIT,  null),
                Arguments.of(FileTranslationExecutor.IMPLICIT,  new String[0]),
                Arguments.of(FileTranslationExecutor.RECURSIVE, null),
                Arguments.of(FileTranslationExecutor.RECURSIVE, new String[0])
        );
    }

}