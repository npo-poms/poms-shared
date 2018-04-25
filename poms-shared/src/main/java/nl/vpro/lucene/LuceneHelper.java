/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.lucene;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

import nl.vpro.util.DateUtils;

@Slf4j
public class LuceneHelper {

    private static final ZoneId ZONE_ID = ZoneId.of("Europe/Amsterdam");

    public static final Version VERSION = Version.LUCENE_5_5_5;

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

        QueryParser parser = new QueryParser(field, analyzer);
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

    public static NumericRangeQuery<Long> createRangeQuery(String field, Instant start, Instant stop, DateTools.Resolution indexResolution) {
        if (start != null) {
            start = DateTools.round(DateUtils.toDate(start), indexResolution).toInstant();
        }

        if (stop != null) {
            stop = DateTools.round(DateUtils.toDate(inc(stop, 1, indexResolution)), indexResolution).toInstant();
        }
        //1471940220000

        return NumericRangeQuery.newLongRange(field,
            start != null ? start.toEpochMilli() : null,
            stop != null ? stop.toEpochMilli() : null,
            true, false);
    }

    public static Instant inc(Instant instant, int amount, DateTools.Resolution indexResolution) {
        Instant nextUnit = instant;
        switch (indexResolution) {
            case YEAR:
                nextUnit = instant.atZone(ZONE_ID).plusYears(amount).toInstant();
                break;
            case MONTH:
                nextUnit = instant.atZone(ZONE_ID).plusMonths(1).toInstant();
                break;
            case DAY:
                nextUnit = instant.plus(amount, ChronoUnit.DAYS);
                break;
            case HOUR:
                nextUnit = instant.plus(amount, ChronoUnit.HOURS);
                break;
            case MINUTE:
                nextUnit = instant.plus(amount, ChronoUnit.MINUTES);
                break;
            case SECOND:
                nextUnit = instant.plusSeconds(amount);
                break;
            case MILLISECOND:
                nextUnit = instant.plusMillis(amount);
                break;
            default:
                log.warn("Unrecognized unit " + indexResolution);
        }
        return nextUnit;
    }

    @Deprecated
    public static NumericRangeQuery createDayRangeQuery(String field, Date start, Date stop, DateTools.Resolution indexResolution) {
        return createRangeQuery(field, DateUtils.toInstant(start), DateUtils.toInstant(stop), indexResolution);
    }


    public static PhraseQuery createPhraseQuery(String field, String phrase, String splitRegex, int slop) {
        String[] words = phrase.split(regex + "|" + splitRegex);

        PhraseQuery.Builder phraseQuery = new PhraseQuery.Builder();
        phraseQuery.setSlop(slop);
        for(String word : words) {
            if(word.length() > 0) {
                phraseQuery.add(new Term(field, word.toLowerCase()));
            }
        }

        return phraseQuery.build();
    }
}
