package io.github.czerepko.i18n.test;

import static java.util.stream.Collectors.toList;

import io.github.czerepko.i18n.common.I18nProperties;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class PropertiesOverWriter {

    private List<String> propertiesFileContent;

    PropertiesOverWriter(PropertiesOverWriterBuilder builder) {
        propertiesFileContent = Stream.of(Pair.of(I18nProperties.FILE_FORMAT,      builder.getFileFormat()),
                                          Pair.of(I18nProperties.FILE_ENCODING,    builder.getFileCharset()),
                                          Pair.of(I18nProperties.PLACEHOLDER_TYPE, builder.getPlaceholderType()),
                                          Pair.of(I18nProperties.LANGUAGE_CODES,   String.join(",", builder.getLanguageCodes())))
                                      .map(propertyAndValue -> String.join("=",
                                                                           propertyAndValue.getLeft()
                                                                                           .getPropertyPlaceholder(),
                                                                           Optional.ofNullable(propertyAndValue.getRight())
                                                                                   .orElse("")))
                                      .map(String::toLowerCase)
                                      .collect(toList());
    }

    public static PropertiesOverWriterBuilder prepare() {
        return new PropertiesOverWriterBuilder();
    }

    public void overwrite() throws IOException {
        URI filePath;
        try {
            filePath = Optional.ofNullable(getClass().getClassLoader().getResource("application.properties"))
                               .orElseThrow(() -> new IllegalStateException("Properties resource file not accessible"))
                               .toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Files.write(Paths.get(filePath), propertiesFileContent);
    }

}
