/**
 * Copyright (C) 2010 All rights reserved VPRO The Netherlands
 */
package nl.vpro.lucene;

import java.io.Reader;
import java.util.Arrays;

import org.apache.lucene.analysis.*;

public final class EntityAnalyser extends Analyzer {

    private static final String[] DUTCH_STOP_WORDS = {
        "aan", "af", "al", "als", "bij", "dan", "dat", "de", "die", "dit", "een", "en",
        "er", "hem", "het", "hij", "hoe", "hun", "ik", "in", "je", "me", "men", "met",
        "mij", "nog", "nu", "of", "ons", "ook", "te", "tot", "uit", "van", "was", "wat",
        "we", "wel", "wij", "zal", "ze", "zij", "zijn", "zo", "zou"
    };
    private static CharArraySet stopWords;

    static {
        CharArraySet stopSet = new CharArraySet(SimpleEntityAnalyser.VERSION, DUTCH_STOP_WORDS.length + StopAnalyzer.ENGLISH_STOP_WORDS_SET.size(), false);
        stopSet.addAll(Arrays.asList(DUTCH_STOP_WORDS));
        stopSet.addAll(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
        stopWords = CharArraySet.unmodifiableSet(stopSet);
    }
    private final SimpleEntityAnalyser wrapped = new SimpleEntityAnalyser();

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        return new StopFilter(
            SimpleEntityAnalyser.VERSION,
            wrapped.tokenStream(fieldName, reader),
            stopWords
        );
    }
}
