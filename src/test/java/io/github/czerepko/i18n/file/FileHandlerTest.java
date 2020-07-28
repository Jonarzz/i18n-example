package io.github.czerepko.i18n.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.google.common.io.Files;
import io.github.czerepko.i18n.common.I18nProperties;
import io.github.czerepko.i18n.test.PropertiesOverWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@DisplayName("File handler tests")
@SuppressWarnings("InnerClassMayBeStatic")
class FileHandlerTest {

    @Nested
    @DisplayName("Positive cases")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class PositiveCasesTest {

        private URL fileDirResourceUrl = getClass().getClassLoader().getResource("file");

        @ParameterizedTest(name = "operation = {1}, charset = {2}")
        @MethodSource("executeWithoutInputParamsProvider")
        @DisplayName("Execute without input")
        <O> void executeWithoutInputTest(FileHandler<?, O> fileHandler, String operationName, Charset charset, O expectedOutput)
                throws IOException, URISyntaxException {
            reloadProperties(charset);
            File file = getResourceFile(operationName, charset);

            O executionResult = fileHandler.execute(file);

            assertEquals(expectedOutput, executionResult);
        }

        Stream<Arguments> executeWithoutInputParamsProvider() {
            return Stream.of(
                    Arguments.of(FileHandler.READ,       "READ",       StandardCharsets.UTF_8, "file content"),
                    Arguments.of(FileHandler.READ_LINES, "READ_LINES", StandardCharsets.UTF_8, List.of("file", "content")),
                    Arguments.of(FileHandler.CREATE,     "CREATE",     StandardCharsets.UTF_8, null),
                    Arguments.of(FileHandler.WRITE,      "WRITE",      StandardCharsets.UTF_8, null),
                    Arguments.of(FileHandler.READ,       "READ",       StandardCharsets.UTF_16, "file content"),
                    Arguments.of(FileHandler.READ_LINES, "READ_LINES", StandardCharsets.UTF_16, List.of("file", "content")),
                    Arguments.of(FileHandler.CREATE,     "CREATE",     StandardCharsets.UTF_16, null),
                    Arguments.of(FileHandler.WRITE,      "WRITE",      StandardCharsets.UTF_16, null)
            );
        }

        @ParameterizedTest(name = "operation = {1}, charset = {3}")
        @MethodSource("executeWithInputParamsProvider")
        @DisplayName("Execute with input")
        void executeWithInputTest(FileHandler<String, ?> fileHandler, String operationName, String input, Charset charset,
                                      TriConsumer<Charset, String, String> verification) throws IOException, URISyntaxException {
            reloadProperties(charset);
            File file = getResourceFile(operationName, charset);

            fileHandler.execute(file, input);

            verification.accept(charset, operationName, input);
        }

        Stream<Arguments> executeWithInputParamsProvider() {
            TriConsumer<Charset, String, String> noContentVerification = (charset, filename, input) -> {};
            TriConsumer<Charset, String, String> contentVerification = (charset, filename, input) -> {
                File file = getResourceFile(filename, charset);
                String actual;
                try {
                    actual = Files.asCharSource(file, charset).readFirstLine();
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
                assertEquals(input, actual);
            };
            return Stream.of(
                    Arguments.of(FileHandler.READ,       "READ",       "read test ąść",   StandardCharsets.UTF_8, noContentVerification),
                    Arguments.of(FileHandler.READ_LINES, "READ_LINES", "read test ąść",   StandardCharsets.UTF_8, noContentVerification),
                    Arguments.of(FileHandler.CREATE,     "CREATE",     "create test ąść", StandardCharsets.UTF_8, contentVerification),
                    Arguments.of(FileHandler.WRITE,      "WRITE",      "write test ąść",  StandardCharsets.UTF_8, contentVerification),
                    Arguments.of(FileHandler.READ,       "READ",       "read test ąść",   StandardCharsets.UTF_16, noContentVerification),
                    Arguments.of(FileHandler.READ_LINES, "READ_LINES", "read test ąść",   StandardCharsets.UTF_16, noContentVerification),
                    Arguments.of(FileHandler.CREATE,     "CREATE",     "create test ąść", StandardCharsets.UTF_16, contentVerification),
                    Arguments.of(FileHandler.WRITE,      "WRITE",      "write test ąść",  StandardCharsets.UTF_16, contentVerification)
            );
        }

        void reloadProperties(Charset charset) throws IOException, URISyntaxException {
            PropertiesOverWriter.prepare()
                                .withFileCharset(charset)
                                .create()
                                .overwrite();
            I18nProperties.reload();
        }

        File getResourceFile(String filename, Charset charset) {
            return Optional.ofNullable(fileDirResourceUrl)
                           .map(url -> new File(url.getFile() + File.separator + filename + "_" + charset))
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