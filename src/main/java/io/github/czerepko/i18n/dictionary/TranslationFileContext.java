package io.github.czerepko.i18n.dictionary;

import static java.lang.System.lineSeparator;

import java.io.File;
import java.util.List;

import io.github.czerepko.i18n.file.FileHandler;

class TranslationFileContext {

    private final File translationFile;

    private List<String> lines;
    private String content;

    TranslationFileContext(File translationFile) {
        this.translationFile = translationFile;
    }

    List<String> getLines() {
        if (lines != null) {
            return lines;
        }
        return lines = FileHandler.READ_LINES.execute(translationFile);
    }

    String getContent() {
        if (content != null) {
            return content;
        }
        return content = String.join(lineSeparator(), getLines());
    }

    String getPath() {
        return translationFile.getPath();
    }

}
