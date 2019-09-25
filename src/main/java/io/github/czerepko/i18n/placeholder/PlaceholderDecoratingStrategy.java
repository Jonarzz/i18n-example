package io.github.czerepko.i18n.placeholder;

import java.util.function.UnaryOperator;

@FunctionalInterface
interface PlaceholderDecoratingStrategy extends UnaryOperator<String> {

    @Override
    String apply(String placeholder);

}
