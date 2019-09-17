package io.github.czerepko.i18n.file;

import org.junit.jupiter.api.DisplayName;

@DisplayName("Properties translations loader tests")
class PropertiesTranslationsLoaderTest extends TranslationsLoaderTest {

    PropertiesTranslationsLoaderTest() {
        super(new PropertiesTranslationsLoader(), "properties");
    }

}
