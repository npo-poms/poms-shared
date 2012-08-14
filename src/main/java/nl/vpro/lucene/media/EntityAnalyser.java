/**
 * Copyright (C) 2010 All rights reserved VPRO The Netherlands
 */
package nl.vpro.lucene.media;

import java.io.Reader;
import java.util.Arrays;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

public final class EntityAnalyser extends Analyzer {

    private static final Version VERSION = Version.LUCENE_30;

    private static final String[] DUTCH_STOP_WORDS = {
        "aan", "af", "al", "als", "bij", "dan", "dat", "de", "die", "dit", "een", "en",
        "er", "hem", "het", "hij", "hoe", "hun", "ik", "in", "je", "me", "men", "met",
        "mij", "nog", "nu", "of", "ons", "ook", "te", "tot", "uit", "van", "was", "wat",
        "we", "wel", "wij", "zal", "ze", "zij", "zijn", "zo", "zou"};
    private static CharArraySet stopWords;

    static {
        CharArraySet stopSet = new CharArraySet(VERSION, DUTCH_STOP_WORDS.length + StopAnalyzer.ENGLISH_STOP_WORDS_SET.size(), false);
        stopSet.addAll(Arrays.asList(DUTCH_STOP_WORDS));
        stopSet.addAll(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
        stopWords = CharArraySet.unmodifiableSet(stopSet);
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        return new StopFilter(
            VERSION,
            new ASCIIFoldingFilter(
                new LowerCaseFilter(
                    VERSION,
                    new StandardFilter(
                        VERSION,
                        new StandardTokenizer(LuceneHelper.VERSION, reader)
                    )
                )
            ),
            stopWords
        );
    }
}
