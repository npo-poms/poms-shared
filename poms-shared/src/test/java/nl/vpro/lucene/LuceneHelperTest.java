/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.lucene;

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
}
