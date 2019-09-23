package io.github.czerepko.i18n.file;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JSON translations loader tests")
class JsonTranslationsLoaderTest extends TranslationsLoaderTest {

    JsonTranslationsLoaderTest() {
        super(new JsonTranslationsLoader(), "json");
    }

    @Test
    @DisplayName("Get translations for directory with multiple files, which contain duplicated keys")
    void getTranslationsForDirectoryWithMultipleFilesWhichContainDuplicatedKeys() {
        var exception = assertThrows(DuplicatedTranslationKeyException.class,
                                     () -> loader.loadTranslations("json_duplicates_in_different_files"));
        assertThat(exception.getMessage(), is(equalTo(
                "File json_duplicates_in_different_files/i18n-with-duplicate.json contains duplicated translation key: 'test.single.ball'"
        )));
    }

}
