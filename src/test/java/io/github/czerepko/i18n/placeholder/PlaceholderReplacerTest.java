package io.github.czerepko.i18n.placeholder;

import static io.github.czerepko.i18n.placeholder.PlaceholderReplacerFactory.TAG;
import static io.github.czerepko.i18n.placeholder.PlaceholderReplacerFactory.VARIABLE;
import static io.github.czerepko.i18n.test.Translations.EXPECTED_CORRECT_ENG;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@DisplayName("Placeholder replacing tests")
class PlaceholderReplacerTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("replacerAndAffixesProvider")
    @DisplayName("Replace placeholders with values")
    void replacePlaceholdersWithValues(PlaceholderReplacerFactory replacerFactory, String prefix, String suffix) {
        PlaceholderReplacer replacer = replacerFactory.create(EXPECTED_CORRECT_ENG);
        EXPECTED_CORRECT_ENG.forEach((placeholder, value) -> {
            String placeholderWithAffixes = prefix + placeholder + suffix;
            String replaced = replacer.replace(placeholderWithAffixes);
            assertThat(replaced, is(equalTo(value)));
        });
    }

    static Stream<Arguments> replacerAndAffixesProvider() {
        return Stream.of(
                Arguments.of(TAG,      "<Trans>", "</Trans>"),
                Arguments.of(VARIABLE, "${",      "}")
        );
    }

}