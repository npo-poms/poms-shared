/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.lucene;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

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
        TermRangeQuery p = LuceneHelper.createRangeQuery("bla",
            LocalDateTime.of(2016, 8, 23, 12, 30).atZone(ZoneId.of("Europe/Amsterdam")).toInstant(),
            LocalDateTime.of(2016, 8, 23, 13, 30).atZone(ZoneId.of("Europe/Amsterdam")).toInstant(),
            DateTools.Resolution.MINUTE);
        assertThat(p.getLowerTerm()).isEqualTo(new BytesRef("201608231030"));
        assertThat(p.getUpperTerm()).isEqualTo(new BytesRef("201608231131"));
    }

    @Test
    public void testRangeQueryDays() throws Exception {
        TermRangeQuery p = LuceneHelper.createRangeQuery("bla",
            LocalDateTime.of(2016, 8, 23, 12, 30).atZone(ZoneId.of("Europe/Amsterdam")).toInstant(),
            LocalDateTime.of(2016, 8, 23, 13, 30).atZone(ZoneId.of("Europe/Amsterdam")).toInstant(),
            DateTools.Resolution.DAY);
        assertThat(p.getLowerTerm()).isEqualTo(new BytesRef("20160823"));
        assertThat(p.getUpperTerm()).isEqualTo(new BytesRef("20160824"));
    }


    @Test
    public void testRangeQueryYear() throws Exception {
        TermRangeQuery p = LuceneHelper.createRangeQuery("bla",
            LocalDateTime.of(2016, 8, 23, 12, 30).atZone(ZoneId.of("Europe/Amsterdam")).toInstant(),
            LocalDateTime.of(2016, 8, 23, 13, 30).atZone(ZoneId.of("Europe/Amsterdam")).toInstant(),
            DateTools.Resolution.YEAR);
        assertThat(p.getLowerTerm()).isEqualTo(new BytesRef("2016"));
        assertThat(p.getUpperTerm()).isEqualTo(new BytesRef("2017"));
    }

    @Test
    public void testRangeQueryMonth() throws Exception {
        TermRangeQuery p = LuceneHelper.createRangeQuery("bla",
            LocalDateTime.of(2016, 8, 23, 12, 30).atZone(ZoneId.of("Europe/Amsterdam")).toInstant(),
            LocalDateTime.of(2016, 8, 23, 13, 30).atZone(ZoneId.of("Europe/Amsterdam")).toInstant(),
            DateTools.Resolution.MONTH);
        assertThat(p.getLowerTerm()).isEqualTo(new BytesRef("201608"));
        assertThat(p.getUpperTerm()).isEqualTo(new BytesRef("201609"));
    }
}
