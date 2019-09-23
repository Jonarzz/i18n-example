package io.github.czerepko.i18n.file;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@DisplayName("Translations loader creating tests")
class TranslationsLoaderProviderTest {

    @ParameterizedTest(name = "type name = ''{0}''")
    @MethodSource("loaderProviderSource")
    @DisplayName("Get loader provider for known type")
    void getLoaderProviderForKnownType(String loaderTypeName, TranslationsLoaderProvider providerType) {
        var provider = TranslationsLoaderProvider.fromString(loaderTypeName);
        assertThat(provider, is(equalTo(providerType)));
    }

    @Test
    @DisplayName("Try to get loader provider for unknown type")
    void tryToGetLoaderProviderForUnknownType() {
        var exception = assertThrows(InvalidTranslationsLoaderTypeException.class,
                                     () -> TranslationsLoaderProvider.fromString("invalid"));

        String exceptionMessage = exception.getMessage();
        assertThat(exceptionMessage, startsWith("Translation loader does not exist for given type 'invalid', available types are: "));

        String[] availableTypes = exceptionMessage.split(": ")[1]
                                                  .replace("[", "")
                                                  .replace("]", "")
                                                  .split(", ");
        assertThat(availableTypes, is(arrayContainingInAnyOrder("json", "yaml", "yml", "property", "properties")));
    }

    @ParameterizedTest(name = "type = ''{0}''")
    @MethodSource("loaderSource")
    @DisplayName("Create loader")
    void createLoader(TranslationsLoaderProvider providerType, Class<I18nTranslationsLoader> loaderType) {
        var loader = providerType.get();
        assertThat(loader, is(instanceOf(loaderType)));
    }

    private static Stream<Arguments> loaderProviderSource() {
        return Stream.of(
                Arguments.of("json",       TranslationsLoaderProvider.JSON),
                Arguments.of("yaml",       TranslationsLoaderProvider.YAML),
                Arguments.of("yml",        TranslationsLoaderProvider.YAML),
                Arguments.of("properties", TranslationsLoaderProvider.PROPERTIES),
                Arguments.of("property",   TranslationsLoaderProvider.PROPERTIES)
        );
    }

    private static Stream<Arguments> loaderSource() {
        return Stream.of(
                Arguments.of(TranslationsLoaderProvider.JSON,       JsonTranslationsLoader.class),
                Arguments.of(TranslationsLoaderProvider.YAML,       YamlTranslationsLoader.class),
                Arguments.of(TranslationsLoaderProvider.PROPERTIES, PropertiesTranslationsLoader.class)
        );
    }

}