package io.github.czerepko.i18n.file;

import org.junit.jupiter.api.DisplayName;

@DisplayName("Yaml translations loader tests")
class YamlTranslationsLoaderTest extends TranslationsLoaderTest {

    YamlTranslationsLoaderTest() {
        super(new YamlTranslationsLoader(), "yml");
    }

}
