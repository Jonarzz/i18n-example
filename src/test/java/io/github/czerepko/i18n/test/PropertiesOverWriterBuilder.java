package io.github.czerepko.i18n.test;

import static java.util.Collections.emptyList;

import io.github.czerepko.i18n.dictionary.TranslationsLoaderProvider;
import io.github.czerepko.i18n.placeholder.PlaceholderReplacerFactory;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class PropertiesOverWriterBuilder {

    private String fileFormat;
    private String fileCharset;
    private String placeholderType;
    private List<String> languageCodes = emptyList();

    PropertiesOverWriterBuilder() {
    }

    public PropertiesOverWriterBuilder withFileFormat(TranslationsLoaderProvider fileFormat) {
        return withFileFormat(fileFormat.name());
    }

    public PropertiesOverWriterBuilder withFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
        return this;
    }

    public PropertiesOverWriterBuilder withFileCharset(Charset fileCharset) {
        return withFileCharset(fileCharset.name());
    }

    public PropertiesOverWriterBuilder withFileCharset(String fileCharset) {
        this.fileCharset = fileCharset;
        return this;
    }

    public PropertiesOverWriterBuilder withPlaceholderType(PlaceholderReplacerFactory placeholderType) {
        return withPlaceholderType(placeholderType.name());
    }

    public PropertiesOverWriterBuilder withPlaceholderType(String placeholderType) {
        this.placeholderType = placeholderType;
        return this;
    }

    public PropertiesOverWriterBuilder withLanguageCodes(String... languageCodes) {
        return withLanguageCodes(Arrays.asList(languageCodes));
    }

    public PropertiesOverWriterBuilder withLanguageCodes(List<String> languageCodes) {
        this.languageCodes = languageCodes;
        return this;
    }

    public PropertiesOverWriter create() {
        return new PropertiesOverWriter(this);
    }

    String getFileFormat() {
        return fileFormat;
    }

    String getFileCharset() {
        return fileCharset;
    }

    String getPlaceholderType() {
        return placeholderType;
    }

    List<String> getLanguageCodes() {
        return languageCodes;
    }

}
