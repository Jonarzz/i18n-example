package io.github.czerepko.i18n.dictionary;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        assertThat(exception.getMessage(), allOf(
                stringContainsInOrder(List.of(
                        "File ",
                        "json_duplicates_in_different_files",
                        " contains duplicated translation key: 'test.single.ball'"
                )),
                anyOf(
                        containsString("i18n.json"),
                        containsString("i18n-with-duplicate.json")
                )
        ));
    }

}
