package io.github.czerepko.i18n.placeholder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

@DisplayName("Placeholder replacer creating tests")
class PlaceholderReplacerFactoryTest {

    @ParameterizedTest(name = "type name = ''{0}''")
    @MethodSource("factorySource")
    @DisplayName("Get replacer factory for known type")
    void getLoaderProviderForKnownType(String typeName, PlaceholderReplacerFactory factory) {
        var provider = PlaceholderReplacerFactory.fromString(typeName);
        assertThat(provider, is(equalTo(factory)));
    }

    private static Stream<Arguments> factorySource() {
        return Stream.of(
                Arguments.of("tag",      PlaceholderReplacerFactory.TAG),
                Arguments.of("variable", PlaceholderReplacerFactory.VARIABLE)
        );
    }

    @Test
    @DisplayName("Try to get replacer factory for unknown type")
    void tryToGetLoaderProviderForUnknownType() {
        var exception = assertThrows(InvalidPlaceholderReplacerTypeException.class,
                                     () -> PlaceholderReplacerFactory.fromString("invalid"));

        String exceptionMessage = exception.getMessage();
        assertThat(exceptionMessage, startsWith("Placeholder replacer does not exist for given type 'invalid', available types are: "));

        String[] availableTypes = exceptionMessage.split(": ")[1]
                                                  .replace("[", "")
                                                  .replace("]", "")
                                                  .split(", ");
        assertThat(availableTypes, is(arrayContainingInAnyOrder("tag", "variable")));
    }

    @ParameterizedTest(name = "type = ''{0}''")
    @EnumSource(PlaceholderReplacerFactory.class)
    @DisplayName("Create replacer")
    void createLoader(PlaceholderReplacerFactory providerType) {
        var replacer = providerType.create(Map.of());
        assertThat(replacer, is(notNullValue()));
    }

}