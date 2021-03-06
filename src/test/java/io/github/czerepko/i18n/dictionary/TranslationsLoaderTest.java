package io.github.czerepko.i18n.dictionary;

import static io.github.czerepko.i18n.test.Translations.EXPECTED_CORRECT_ENG;
import static io.github.czerepko.i18n.test.Translations.EXPECTED_CORRECT_POL;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.czerepko.i18n.common.I18nProperties;
import io.github.czerepko.i18n.test.PropertiesOverWriter;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

abstract class TranslationsLoaderTest {

    private String fileExtension;

    I18nTranslationsLoader loader;

    TranslationsLoaderTest(I18nTranslationsLoader loader, String fileExtension) {
        this.loader = loader;
        this.fileExtension = fileExtension;
    }

    @BeforeEach
    void prepareProperties() throws IOException {
        PropertiesOverWriter.prepare()
                            .withFileFormat(fileExtension)
                            .create()
                            .overwrite();
        I18nProperties.reload();
    }

    @ParameterizedTest(name = "language = {0}")
    @MethodSource("correctTranslationsMethodSource")
    @DisplayName("Get translations from a correct file")
    void getEnglishTranslationsFromTheCorrectFile(String languageCode, Map<String, String> expectedTranslations) {
        Map<String, String> translations = loader.loadTranslations(languageCode);
        assertThat(translations, allOf(entriesMatch(expectedTranslations)));
    }

    static Stream<Arguments> correctTranslationsMethodSource() {
        return Stream.of(
                Arguments.of("eng", EXPECTED_CORRECT_ENG),
                Arguments.of("pol", EXPECTED_CORRECT_POL)
        );
    }

    List<Matcher<? super Map<String, String>>> entriesMatch(Map<String, String> expected) {
        return expected.entrySet()
                       .stream()
                       .map(entry -> hasEntry(entry.getKey(), entry.getValue()))
                       .collect(toList());
    }

    @Test
    @DisplayName("Get translations for directory with file with duplicated keys")
    void getTranslationsForDirectoryWithDuplicatedKeys() {
        var exception = assertThrows(DuplicatedTranslationKeyException.class,
                                     () -> loader.loadTranslations("eng_with_duplicates"));
        assertThat(exception.getMessage(), stringContainsInOrder(List.of(
                "File ",
                "eng_with_duplicates",
                String.format("i18n.%s contains duplicated translation key: 'test.single.ball'", fileExtension)
        )));
    }

    @Test
    @DisplayName("Try to get translations for not existing directory")
    void tryToGetTranslationsForNotExistingDirectory() {
        var exception = assertThrows(MissingTranslationResourcesException.class,
                                     () -> loader.loadTranslations("missing"));
        assertThat(exception.getMessage(), is(equalTo(
                "Missing translations resources. "
                + "Please, make sure that subdirectory with name 'missing' containing translation files exists "
                + "in the 'i18n' resources directory. "
                + "The files should have extensions appropriate for file format defined in the properties file."
        )));
    }

    @Test
    @DisplayName("Try to get translations for empty directory")
    void tryToGetTranslationsForEmptyDirectory() {
        var exception = assertThrows(MissingTranslationResourcesException.class,
                                     () -> loader.loadTranslations("empty"));
        assertThat(exception.getMessage(), is(equalTo(
                "Missing translations resources."
                + " Please, make sure that subdirectory with name 'empty' containing translation files exists "
                + "in the 'i18n' resources directory. "
                + "The files should have extensions appropriate for file format defined in the properties file."
        )));
    }

    @Test
    @DisplayName("Try to get translations for directory with invalid file format")
    void tryToGetTranslationsForDirectoryWithInvalidFileFormat() {
        var exception = assertThrows(InvalidTranslationsFileFormatException.class,
                                     () -> loader.loadTranslations("invalid_file_format"));
        assertThat(exception.getMessage(), stringContainsInOrder(List.of(
                "File ",
                "invalid_file_format",
                String.format("i18n.%s has invalid format", fileExtension)
        )));
    }

}