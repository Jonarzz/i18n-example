package io.github.czerepko.i18n.file;

import org.junit.jupiter.api.DisplayName;

@DisplayName("JSON translations loader tests")
class JsonTranslationsLoaderTest extends TranslationsLoaderTest {

    JsonTranslationsLoaderTest() {
        super(new JsonTranslationsLoader(), "json");
    }

}
