package io.github.czerepko.i18n.dictionary;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@DisplayName("Translation file context tests")
class TranslationFileContextTest {

    private static final String FILE_PATH = "i18n/eng/i18n.properties";
    private static final File FILE = Optional.ofNullable(TranslationFileContextTest.class.getClassLoader()
                                                                                         .getResource(FILE_PATH))
                                             .map(URL::getFile)
                                             .map(File::new)
                                             .orElseThrow(() -> new IllegalStateException("Resource " + FILE_PATH + " not accessible"));

    @Test
    @DisplayName("Get lines from file")
    void getLines() {
        TranslationFileContext fileContext = new TranslationFileContext(FILE);

        List<String> lines = fileContext.getLines();

        assertThat(lines, is(notNullValue()));
        assertThat(lines.size(), is(greaterThan(0)));

        List<String> cachedLines = fileContext.getLines();
        assertThat(cachedLines, is(sameInstance(lines)));
    }

    @Test
    @DisplayName("Get content from file")
    void getContent() {
        TranslationFileContext fileContext = new TranslationFileContext(FILE);

        String content = fileContext.getContent();

        assertThat(content, is(notNullValue()));
        assertThat(content.length(), is(greaterThan(0)));

        String cachedContent = fileContext.getContent();
        assertThat(cachedContent, is(sameInstance(content)));
    }

    @Test
    @DisplayName("Get path of the file")
    void getPath() {
        TranslationFileContext fileContext = new TranslationFileContext(FILE);

        String path = fileContext.getPath();
        assertThat(path, is(equalTo(FILE.getPath())));
    }

}