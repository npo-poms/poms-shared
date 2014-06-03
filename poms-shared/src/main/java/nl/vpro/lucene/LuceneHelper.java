/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.lucene;

import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneHelper {
    private static final Logger log = LoggerFactory.getLogger(LuceneHelper.class);

    public static final Version VERSION = Version.LUCENE_29; // Newer version make searching behave differently (and fail test)

    // Disallowed input. Only allowing phrase and wildcard queries with: " , * and ?.

    private static final String[] ESCAPE_STRINGS = {
        "+", "-", "&&", "||", "*", "?", "!", "(", ")", "{", "}", "[", "]", "^", "~", ":", "\\"
    };

    private static final int STANDARD_SLOP = 3;

    private static String regex = "";

    static {
        for(String s : ESCAPE_STRINGS) {
            regex += (s.equals(ESCAPE_STRINGS[0]) ? "" : "|") + "\\" + s.substring(0, 1);
        }
    }

    public static String escape(String input) {
        return input.replaceAll("(?<!\\\\)(" + regex + ")", "\\\\$1").trim();
    }

    public static Query createStandardQuery(String field, String text, Analyzer analyzer) {
        text = escape(text);

        QueryParser parser = new QueryParser(VERSION, field, analyzer);
        parser.setPhraseSlop(STANDARD_SLOP);
        parser.setEnablePositionIncrements(true);
//            parser.setAllowLeadingWildcard(true);

        try {
            return parser.parse(text);
        } catch(ParseException e) {
            log.warn("Exception parsing query text: \"{}\", returning all documents.", text);
            return new MatchAllDocsQuery();
        }
    }

    /**
     * Creates a range query with a resolution of a day by adding 24 hours to the stop date. Is primarily used for
     * handling form input fields where a date (i.e. yyyyMMdd) is entered with an inclusive stop date.
     *
     * @param field           - the field to query
     * @param start           - inclusive start or null
     * @param stop            - inclusive stop or null
     * @param indexResolution - the resolution of the index which can be more precise when you want to order with
     *                        more precision
     * @return The create TempRangeQuery
     */

    public static TermRangeQuery createDayRangeQuery(String field, Date start, Date stop, DateTools.Resolution indexResolution) {

        String lower = null;
        if(start != null) {
            lower = DateTools.dateToString(start, indexResolution);
        }

        String upper = null;
        if(stop != null) {
            Date nextDay = new Date(stop.getTime() + 24 * 60 * 60 * 1000);
            upper = DateTools.dateToString(nextDay, indexResolution);
        }

        return new TermRangeQuery(field, lower, upper, true, false);
    }

    public static PhraseQuery createPhraseQuery(String field, String phrase, String splitRegex, int slop) {
        String[] words = phrase.split(regex + "|" + splitRegex);

        PhraseQuery phraseQuery = new PhraseQuery();
        phraseQuery.setSlop(slop);
        for(String word : words) {
            if(word.length() > 0) {
                phraseQuery.add(new Term(field, word.toLowerCase()));
            }
        }

        return phraseQuery;
    }
}
