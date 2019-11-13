package io.github.czerepko.i18n.file;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class FileHandler<I, O> {

    public static final FileHandler<Void, String> READ = new FileHandler<>(FileOperation.READ,
            file -> Files.asCharSource(file, Charsets.UTF_8).read());
    public static final FileHandler<Void, Boolean> CREATE = new FileHandler<>(FileOperation.CREATE,
            file -> file.exists() || file.createNewFile());
    public static final FileHandler<String, Void> WRITE = new FileHandler<>(FileOperation.WRITE,
            (file, payloadToSave) -> Files.asCharSink(file, Charsets.UTF_8)
                                          .write(Optional.ofNullable(payloadToSave)
                                                         .orElse("")));

    @FunctionalInterface
    private interface FileHandlingFunction<O> {
        O apply(File file) throws IOException;
    }

    @FunctionalInterface
    private interface FileHandlingConsumerWithInput<I> {
        void apply(File file, I input) throws IOException;
    }

    private FileOperation operationType;
    private FileHandlingFunction<O> guardedFunction;
    private FileHandlingConsumerWithInput<I> guardedBiConsumer;

    private FileHandler(FileOperation operationType, FileHandler.FileHandlingFunction<O> guardedFunction) {
        this.operationType = operationType;
        this.guardedFunction = guardedFunction;
        guardedBiConsumer = (file, alsoIgnored) -> guardedFunction.apply(file);
    }

    private FileHandler(FileOperation operationType, FileHandler.FileHandlingConsumerWithInput<I> guardedBiFunction) {
        this.operationType = operationType;
        this.guardedBiConsumer = guardedBiFunction;
        guardedFunction = file -> {
            guardedBiConsumer.apply(file, null);
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
            guardedBiConsumer.apply(file, input);
        } catch (IOException ioException) {
            throw new FileHandlingException(ioException, operationType);
        }
    }

}
