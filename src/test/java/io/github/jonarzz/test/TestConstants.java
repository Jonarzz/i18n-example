package io.github.jonarzz.test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestConstants {

    public static final String ENGLISH_LANGUAGE = "eng";
    public static final List<String> ENGLISH_DICTIONARY_FILE_CONTENT_LINES = List.of(
            "fruit.apple = apple",
            "fruit.pear  = pear",
            "sentence.short = This is a short sentence",
            "sentence.long  = This is an example of a long sentence"
    );
    public static final String POLISH_LANGUAGE = "pol";
    public static final List<String> POLISH_DICTIONARY_FILE_CONTENT_LINES = List.of(
            "fruit.apple = jabłko",
            "fruit.pear  = gruszka",
            "sentence.short = To jest krótkie zdanie",
            "sentence.long  = To jest przykład długiego zdania"
    );
    public static final Collection<String> LANGUAGE_NAMES = Set.of(ENGLISH_LANGUAGE, POLISH_LANGUAGE);

    public static final String TEMPLATE_1_FILE_NAME = "template1";
    public static final List<String> TEMPLATE_1_FILE_CONTENT_LINES = List.of(
            "${fruit.apple} ${fruit.pear}",
            "${sentence.short}. ${sentence.long}."
    );
    public static final String TEMPLATE_2_FILE_NAME = "template2";
    public static final List<String> TEMPLATE_2_FILE_CONTENT_LINES = List.of(
            "${sentence.short}. ${sentence.long}.",
            "${fruit.apple} ${fruit.pear}"
    );
    public static final Collection<String> TEMPLATE_FILE_NAMES = Set.of(TEMPLATE_1_FILE_NAME, TEMPLATE_2_FILE_NAME);

    public static final Map<String, List<String>> TRANSLATED_FILENAME_TO_EXPECTED_CONTENT_LINES = Map.of(
            translatedFileName(TEMPLATE_1_FILE_NAME, ENGLISH_LANGUAGE),
            List.of(
                    "apple pear",
                    "This is a short sentence. This is an example of a long sentence."
            ),
            translatedFileName(TEMPLATE_1_FILE_NAME, POLISH_LANGUAGE),
            List.of(
                    "jabłko gruszka",
                    "To jest krótkie zdanie. To jest przykład długiego zdania."
            ),
            translatedFileName(TEMPLATE_2_FILE_NAME, ENGLISH_LANGUAGE),
            List.of(
                    "This is a short sentence. This is an example of a long sentence.",
                    "apple pear"
            ),
            translatedFileName(TEMPLATE_2_FILE_NAME, POLISH_LANGUAGE),
            List.of(
                    "To jest krótkie zdanie. To jest przykład długiego zdania.",
                    "jabłko gruszka"
            )
    );

    private TestConstants() {
    }

    public static String propertiesFileName(String language) {
        return language + ".properties";
    }

    public static String translatedFileName(String templateFileName, String language) {
        return templateFileName + "-" + language;
    }

}
