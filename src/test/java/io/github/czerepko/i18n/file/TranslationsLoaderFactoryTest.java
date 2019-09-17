package io.github.czerepko.i18n.file;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@DisplayName("Translations loader creating tests")
class TranslationsLoaderFactoryTest {

    @ParameterizedTest(name = "type name = ''{0}''")
    @MethodSource("loaderFactorySource")
    @DisplayName("Get loader factory for known type")
    void getLoaderFactoryForKnownType(String loaderTypeName, TranslationsLoaderFactory factoryType) {
        var factory = TranslationsLoaderFactory.fromString(loaderTypeName);
        assertThat(factory, is(equalTo(factoryType)));
    }

    @Test
    @DisplayName("Try to get loader factory for unknown type")
    void tryToGetLoaderFactoryForUnknownType() {
        var exception = assertThrows(InvalidTranslationLoaderTypeException.class,
                                     () -> TranslationsLoaderFactory.fromString("this value is invalid"));
        assertThat(exception.getMessage(), is(equalTo("Translation loader does not exist for given type: this value is invalid")));
    }

    @ParameterizedTest(name = "type = ''{0}''")
    @MethodSource("loaderSource")
    @DisplayName("Create loader")
    void createLoader(TranslationsLoaderFactory factoryType, Class<I18nTranslationsLoader> loaderType) {
        var loader = factoryType.create();
        assertThat(loader, is(notNullValue()));
        assertThat(loader, is(instanceOf(loaderType)));
    }

    private static Stream<Arguments> loaderFactorySource() {
        return Stream.of(
                Arguments.of("json",       TranslationsLoaderFactory.JSON),
                Arguments.of("yaml",       TranslationsLoaderFactory.YAML),
                Arguments.of("yml",        TranslationsLoaderFactory.YAML),
                Arguments.of("properties", TranslationsLoaderFactory.PROPERTIES),
                Arguments.of("property",   TranslationsLoaderFactory.PROPERTIES)
        );
    }

    private static Stream<Arguments> loaderSource() {
        return Stream.of(
                Arguments.of(TranslationsLoaderFactory.JSON,       JsonTranslationsLoader.class),
                Arguments.of(TranslationsLoaderFactory.YAML,       YamlTranslationsLoader.class),
                Arguments.of(TranslationsLoaderFactory.PROPERTIES, PropertiesTranslationsLoader.class)
        );
    }


}