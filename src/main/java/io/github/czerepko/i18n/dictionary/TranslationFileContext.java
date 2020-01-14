package io.github.czerepko.i18n.dictionary;

import static java.lang.System.lineSeparator;

import com.google.common.base.Joiner;
import io.github.czerepko.i18n.file.FileHandler;

import java.io.File;
import java.util.List;

class TranslationFileContext {

    private File translationFile;

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
        return content = Joiner.on(lineSeparator()).join(getLines());
    }

    String getPath() {
        return translationFile.getPath();
    }

}
