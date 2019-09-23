package io.github.czerepko.i18n.test;

import java.util.Map;

public class Translations {

    public static final Map<String, String> EXPECTED_CORRECT_ENG = Map.of(
            "test.single.ball",    "ball",
            "test.single.bird",    "bird",
            "test.single.table",   "table",
            "test.sentence.long",  "This is a pretty long sentence, which is used in the test",
            "test.sentence.short", "This is a short sentence"
    );

    public static final Map<String, String> EXPECTED_CORRECT_POL = Map.of(
            "test.single.ball",    "piłka",
            "test.single.bird",    "ptak",
            "test.single.table",   "stół",
            "test.sentence.long",  "To jest dosyć długie zdanie, które jest wykorzystywane w teście",
            "test.sentence.short", "To jest krótkie zdanie"
    );

}
