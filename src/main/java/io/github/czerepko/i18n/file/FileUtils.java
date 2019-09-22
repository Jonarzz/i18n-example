package io.github.czerepko.i18n.file;

import static java.lang.System.lineSeparator;

import com.google.common.base.Joiner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

class FileUtils {

    private FileUtils() {

    }

    static List<String> getLines(File file) {
        try {
            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static String getContent(File file) {
        return Joiner.on(lineSeparator()).join(getLines(file));
    }

}
