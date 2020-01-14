package io.github.czerepko.i18n.file;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class FileHandler<I, O> {

    public static final FileHandler<Void, String> READ = new FileHandler<>(
            FileOperation.READ,
            file -> Files.asCharSource(file, Charsets.UTF_8)
                         .read()
    );
    @SuppressWarnings("UnstableApiUsage")
    public static final FileHandler<Void, List<String>> READ_LINES = new FileHandler<>(
            FileOperation.READ,
            file -> Files.readLines(file, Charsets.UTF_8)
    );
    public static final FileHandler<String, Void> WRITE = new FileHandler<>(
            FileOperation.WRITE,
            (file, content) -> Files.asCharSink(file, Charsets.UTF_8)
                                    .write(Optional.ofNullable(content)
                                                   .orElse(""))
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

    @FunctionalInterface
    private interface FileHandlingFunction<O> {

        O apply(File file) throws IOException;
    }

    @FunctionalInterface
    private interface FileHandlingConsumerWithInput<I> {

        void apply(File file, I input) throws IOException;
    }

}
