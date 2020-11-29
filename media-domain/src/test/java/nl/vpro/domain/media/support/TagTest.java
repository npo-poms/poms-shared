/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import org.junit.jupiter.api.Test;
import org.junit.experimental.theories.DataPoint;

import nl.vpro.test.theory.ComparableTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TagTest extends ComparableTest<Tag> {

    @DataPoint
    public static Tag nullArgument = null;

    @DataPoint
    public static Tag textOnly = new Tag("tag");

    @DataPoint
    public static Tag textOnly2 = new Tag("tag");

    @DataPoint
    public static Tag anotherTag = new Tag("tag2");

    @Test
    public void testCaseSensitivity() {
        Tag t1 = new Tag("tag");
        Tag t2 = new Tag("Tag");

        assertThat(t1.equals(t2)).isFalse();
    }
}
