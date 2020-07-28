package io.github.czerepko.i18n.test;

import static java.util.stream.Collectors.toList;

import com.google.common.base.Joiner;
import com.google.common.io.Resources;
import io.github.czerepko.i18n.common.I18nProperties;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class PropertiesOverWriter {

    private List<String> propertiesFileContent;

    PropertiesOverWriter(PropertiesOverWriterBuilder builder) {
        propertiesFileContent = Stream.of(Pair.of(I18nProperties.FILE_FORMAT,      builder.getFileFormat()),
                                          Pair.of(I18nProperties.FILE_ENCODING,    builder.getFileCharset()),
                                          Pair.of(I18nProperties.PLACEHOLDER_TYPE, builder.getPlaceholderType()),
                                          Pair.of(I18nProperties.LANGUAGE_CODES,   Joiner.on(',')
                                                                                         .join(builder.getLanguageCodes())))
                                      .map(propertyAndValue -> Joiner.on('=')
                                                                     .skipNulls()
                                                                     .join(propertyAndValue.getLeft().getPropertyPlaceholder(),
                                                                           propertyAndValue.getRight()))
                                      .map(String::toLowerCase)
                                      .collect(toList());
    }

    public static PropertiesOverWriterBuilder prepare() {
        return new PropertiesOverWriterBuilder();
    }

    @SuppressWarnings("UnstableApiUsage")
    public void overwrite() throws IOException, URISyntaxException {
        URI filePath = Resources.getResource("application.properties").toURI();
        Files.write(Paths.get(filePath), propertiesFileContent);
    }

}
