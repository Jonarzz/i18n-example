package io.github.czerepko.i18n.file;

import static java.util.function.Predicate.not;

import io.github.czerepko.i18n.common.I18nProperties;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

public class FileHandler<I, O> {

    public static final FileHandler<Void, String> READ = new FileHandler<>(
            FileOperation.READ,
            file -> Files.readString(file.toPath(), charsetFromProperties())
    );
    public static final FileHandler<Void, List<String>> READ_LINES = new FileHandler<>(
            FileOperation.READ_LINES,
            file -> Files.readAllLines(file.toPath(), charsetFromProperties())
    );
    public static final FileHandler<String, Void> WRITE = new FileHandler<>(
            FileOperation.WRITE,
            (file, content) -> Files.write(file.toPath(),
                                           Optional.ofNullable(content)
                                                   .orElse("")
                                                   .getBytes(charsetFromProperties()))
    );
    public static final FileHandler<String, Void> CREATE = new FileHandler<>(
            FileOperation.CREATE,
            (file, content) -> {
                if (file.exists() || file.createNewFile()) {
                    WRITE.execute(file, content);
                }
            }
    );

    private FileOperation operationType;
    private FileHandlingFunction<O> guardedFunction;
    private FileHandlingConsumerWithInput<I> guardedBiFunction;

    private FileHandler(FileOperation operationType, FileHandler.FileHandlingFunction<O> guardedFunction) {
        this.operationType = operationType;
        this.guardedFunction = guardedFunction;
        guardedBiFunction = (file, ignored) -> guardedFunction.apply(file);
    }

    private FileHandler(FileOperation operationType, FileHandler.FileHandlingConsumerWithInput<I> guardedBiFunction) {
        this.operationType = operationType;
        this.guardedBiFunction = guardedBiFunction;
        guardedFunction = file -> {
            this.guardedBiFunction.apply(file, null);
            return null;
        };
    }

    public O execute(File file) {
        try {
            return guardedFunction.apply(file);
        } catch (IOException ioException) {
            throw new FileHandlingException(ioException, operationType);
        }
    }

    public void execute(File file, I input) {
        try {
            guardedBiFunction.apply(file, input);
        } catch (IOException ioException) {
            throw new FileHandlingException(ioException, operationType);
        }
    }

    private static Charset charsetFromProperties() {
        return Optional.ofNullable(I18nProperties.FILE_ENCODING.getValue())
                       .filter(not(String::isBlank))
                       .map(Charset::forName)
                       .orElse(StandardCharsets.UTF_8);
    }

    @FunctionalInterface
    private interface FileHandlingFunction<O> {

        O apply(File file) throws IOException;

    }

    @FunctionalInterface
    private interface FileHandlingConsumerWithInput<I> {

        void apply(File file, I input) throws IOException;

    }

}
