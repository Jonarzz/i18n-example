package io.github.czerepko.i18n.placeholder;

@FunctionalInterface
interface PlaceholderDecoratingStrategy {

    String apply(String placeholder);

}
