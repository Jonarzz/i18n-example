package io.github.czerepko.i18n.file;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.google.common.io.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

@DisplayName("File handler tests")
@SuppressWarnings("InnerClassMayBeStatic")
class FileHandlerTest {

    @Nested
    @DisplayName("Positive cases")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PositiveCasesTest {

        private URL fileDirResourceUrl = getClass().getClassLoader().getResource("file");

        @ParameterizedTest(name = "operation = {1}")
        @MethodSource("executeWithoutInputParamsProvider")
        @DisplayName("Execute without input")
        <O> void executeWithoutInputTest(FileHandler<?, O> fileHandler, String operationName, O expectedOutput) {
            File file = getResourceFile(operationName);

            O executionResult = fileHandler.execute(file);

            assertEquals(expectedOutput, executionResult);
        }

        Stream<Arguments> executeWithoutInputParamsProvider() {
            return Stream.of(
                    Arguments.of(FileHandler.READ,       "READ",       "file content"),
                    Arguments.of(FileHandler.READ_LINES, "READ_LINES", List.of("file", "content")),
                    Arguments.of(FileHandler.CREATE,     "CREATE",     null),
                    Arguments.of(FileHandler.WRITE,      "WRITE",      null)
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

        Stream<Arguments> executeWithInputParamsProvider() {
            BiConsumer noContentVerification = (filename, input) -> {};
            BiConsumer<String, String> contentVerification = (filename, input) -> {
                File file = getResourceFile(filename);
                String actual;
                try {
                    actual = Files.asCharSource(file, UTF_8).readFirstLine();
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
                assertEquals(input, actual);
            };
            return Stream.of(
                    Arguments.of(FileHandler.READ,       "READ",       "read test",   noContentVerification),
                    Arguments.of(FileHandler.READ_LINES, "READ_LINES", "read test",   noContentVerification),
                    Arguments.of(FileHandler.CREATE,     "CREATE",     "create test", contentVerification),
                    Arguments.of(FileHandler.WRITE,      "WRITE",      "write test",  contentVerification)
            );
        }

        File getResourceFile(String filename) {
            return Optional.ofNullable(fileDirResourceUrl)
                           .map(url -> new File(url.getFile() + File.separator + filename))
                           .orElseThrow(AssertionError::new);
        }

    }

    @Nested
    @DisplayName("Negative cases")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class NegativeCasesTest {

        private File ioExceptionThrowingFile = mock(File.class, invocation -> {
            throw new IOException();
        });

        @ParameterizedTest(name = "operation = {1}")
        @MethodSource("executeWithErrorProvider")
        @DisplayName("IOException handling in execute without input test")
        void ioExceptionInExecuteWithoutInputTest(FileHandler<?, ?> fileHandler, String operationName) {
            Exception customException = assertThrows(FileHandlingException.class,
                                                     () -> fileHandler.execute(ioExceptionThrowingFile));

            assertEquals(String.format("An error occurred while performing %s operation on a file", operationName),
                         customException.getMessage());
        }

        @ParameterizedTest(name = "operation = {1}")
        @MethodSource("executeWithErrorProvider")
        @DisplayName("IOException handling in execute with input test")
        void ioExceptionInExecuteWithInputTest(FileHandler<?, ?> fileHandler, String operationName) {
            Exception customException = assertThrows(FileHandlingException.class,
                                                     () -> fileHandler.execute(ioExceptionThrowingFile, null));

            assertEquals(String.format("An error occurred while performing %s operation on a file", operationName),
                         customException.getMessage());
        }

        Stream<Arguments> executeWithErrorProvider() {
           return Stream.of(
                   Arguments.of(FileHandler.CREATE,     "create"),
                   Arguments.of(FileHandler.READ,       "read"),
                   Arguments.of(FileHandler.READ_LINES, "read lines"),
                   Arguments.of(FileHandler.WRITE,      "write")
           );
       }

    }

}