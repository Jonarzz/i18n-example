package io.github.czerepko.i18n.file;

import static java.lang.System.lineSeparator;

import com.google.common.base.Joiner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

class TranslationFileContext {

    private String languageCode;
    private File translationFile;

    private List<String> lines;
    private String content;

    TranslationFileContext(String languageCode, File translationFile) {
        this.languageCode = languageCode;
        this.translationFile = translationFile;
    }

    List<String> getLines() {
        if (lines != null) {
            return lines;
        }
        try {
            return lines = Files.readAllLines(translationFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String getContent() {
        if (content != null) {
            return content;
        }
        return content = Joiner.on(lineSeparator()).join(getLines());
    }

    String getPath() {
        return languageCode + "/" + translationFile.getName();
    }

}
