/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.lucene;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.search.NumericRangeQuery;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class LuceneHelperTest {

    @Test
    public void testEscape() throws Exception {
        class Pair {
            String input;

            String escaped;

            Pair(String input, String escaped) {
                this.input = input;
                this.escaped = escaped;
            }
        }

        Pair[] pairs = {
            new Pair("*Hoe?zo!", "\\*Hoe\\?zo\\!"),
            new Pair("+/-", "\\+/\\-"),
            new Pair("and && or ||", "and \\&\\& or \\|\\|"),
            new Pair("(aaa)", "\\(aaa\\)"),
            new Pair("{aaa}", "\\{aaa\\}"),
            new Pair("~[^abc]", "\\~\\[\\^abc\\]"),
            new Pair("a: \\a", "a\\: \\\\a")
        };

        for(Pair pair : pairs) {
            assertThat(LuceneHelper.escape(pair.input)).isEqualTo(pair.escaped);
        }
    }


    @Test
    public void testRangeQueryMinutes() throws Exception {
        Instant start = LocalDateTime.of(2016, 8, 23, 12, 30).atZone(ZoneId.of("Europe/Amsterdam")).toInstant();
        Instant stop  = LocalDateTime.of(2016, 8, 23, 13, 30).atZone(ZoneId.of("Europe/Amsterdam")).toInstant();
        NumericRangeQuery<Long> p = LuceneHelper.createRangeQuery("bla",
            start,
            stop,
            DateTools.Resolution.MINUTE);
        assertThat(p.getMin()).isEqualTo(1471948200000L);
        assertThat(p.getMax()).isEqualTo(1471951860000L);
    }

    @Test
    public void testRangeQueryDays() throws Exception {
        Instant start = LocalDateTime.of(2016, 8, 23, 12, 30).atZone(ZoneId.of("Europe/Amsterdam")).toInstant();
        Instant stop  = LocalDateTime.of(2016, 8, 23, 13, 30).atZone(ZoneId.of("Europe/Amsterdam")).toInstant();
        NumericRangeQuery<Long> p = LuceneHelper.createRangeQuery("bla",
            start,
            stop,
            DateTools.Resolution.DAY);
        assertThat(p.getMin()).isEqualTo(1471910400000L);
        assertThat(p.getMax()).isEqualTo(1471996800000L);
    }


    @Test
    public void testRangeQueryYear() throws Exception {
        Instant start = LocalDateTime.of(2016, 8, 23, 12, 30).atZone(ZoneId.of("Europe/Amsterdam")).toInstant();
        Instant stop  = LocalDateTime.of(2016, 8, 23, 13, 30).atZone(ZoneId.of("Europe/Amsterdam")).toInstant();
        NumericRangeQuery<Long> p = LuceneHelper.createRangeQuery("bla",
            start,
            stop,
            DateTools.Resolution.YEAR);
        assertThat(p.getMin()).isEqualTo(1451606400000L);
        assertThat(p.getMax()).isEqualTo(1483228800000L);
    }

    @Test
    public void testRangeQueryMonth() throws Exception {
        NumericRangeQuery<Long> p = LuceneHelper.createRangeQuery("bla",
            LocalDateTime.of(2016, 8, 23, 12, 30).atZone(ZoneId.of("Europe/Amsterdam")).toInstant(),
            LocalDateTime.of(2016, 8, 23, 13, 30).atZone(ZoneId.of("Europe/Amsterdam")).toInstant(),
            DateTools.Resolution.MONTH);
        assertThat(p.getMin()).isEqualTo(1470009600000L);
        assertThat(p.getMax()).isEqualTo(1472688000000L);
    }
}
