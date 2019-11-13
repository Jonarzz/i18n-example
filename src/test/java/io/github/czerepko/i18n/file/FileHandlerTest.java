package io.github.czerepko.i18n.file;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.google.common.io.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

@DisplayName("File handler tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileHandlerTest {

    private static final File IO_EXCEPTION_THROWING_FILE = mock(File.class, invocation -> {
        throw new IOException();
    });

    private URL fileDirResourceUrl = getClass().getClassLoader().getResource("file");

    @ParameterizedTest(name = "operation = {1}")
    @MethodSource("executeWithoutInputParamsProvider")
    @DisplayName("Execute without input")
    <O> void executeWithoutInputTest(FileHandler<?, O> fileHandler, String operationName, O expectedOutput) {
        File file = getResourceFile(operationName);

        O executionResult = fileHandler.execute(file);

        assertEquals(expectedOutput, executionResult);
    }

    private static Stream<Arguments> executeWithoutInputParamsProvider() {
        return Stream.of(
                Arguments.of(FileHandler.READ,   "READ",   "file content"),
                Arguments.of(FileHandler.CREATE, "CREATE", true),
                Arguments.of(FileHandler.WRITE,  "WRITE",  null)
        );
    }

    @ParameterizedTest(name = "operation = {1}")
    @MethodSource("executeWithInputParamsProvider")
    @DisplayName("Execute with input")
    <I> void executeWithInputTest(FileHandler<I, ?> fileHandler, String operationName, I input, BiConsumer<String, I> verification) {
        File file = getResourceFile(operationName);

        fileHandler.execute(file, input);

        verification.accept(operationName, input);
    }

    private Stream<Arguments> executeWithInputParamsProvider() {
        return Stream.of(
                Arguments.of(FileHandler.READ,   "READ",    null,  (BiConsumer) (filename, input) -> {}),
                Arguments.of(FileHandler.CREATE, "CREATE",  null,  (BiConsumer) (filename, input) -> {}),
                Arguments.of(FileHandler.WRITE,  "WRITE",  "test", (BiConsumer<String, String>) (filename, input) -> {
                    File file = getResourceFile(filename);
                    String actual;
                    try {
                        actual = Files.asCharSource(file, UTF_8).readFirstLine();
                    } catch (IOException e) {
                        throw new AssertionError(e);
                    }
                    assertEquals(input, actual);
                })
        );
    }

    @ParameterizedTest(name = "operation = {1}")
    @MethodSource("executeWithoutInputHandlerProvider")
    @DisplayName("IOException handling in execute without input test")
    void ioExceptionInExecuteWithoutInputTest(FileHandler<?, ?> fileHandler, String operationName) {
        Exception customException = assertThrows(FileHandlingException.class,
                                                 () -> fileHandler.execute(IO_EXCEPTION_THROWING_FILE));

        assertEquals(String.format("An error occurred while performing a %s operation on a file", operationName.toLowerCase()),
                     customException.getMessage());
    }

    private Stream<Arguments> executeWithoutInputHandlerProvider() {
        return Stream.of(
                Arguments.of(FileHandler.CREATE, "CREATE"),
                Arguments.of(FileHandler.READ,   "READ")
        );
    }

    @Test
    @DisplayName("IOException handling in execute with input test")
    void ioExceptionInExecuteWithInputTest() {
        Exception customException = assertThrows(FileHandlingException.class,
                                                 () -> FileHandler.WRITE.execute(IO_EXCEPTION_THROWING_FILE, ""));

        assertEquals("An error occurred while performing a write operation on a file",
                     customException.getMessage());
    }

    private File getResourceFile(String filename) {
        return Optional.ofNullable(fileDirResourceUrl)
                       .map(url -> new File(url.getFile() + File.separator + filename))
                       .orElseThrow(AssertionError::new);
    }

}