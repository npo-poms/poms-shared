/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import org.junit.jupiter.api.Test;
import org.meeuw.util.test.ComparableTheory;

import static org.assertj.core.api.Assertions.assertThat;

public class TagTest implements ComparableTheory<Tag> {

    @Test
    public void testCaseSensitivity() {
        Tag t1 = new Tag("tag");
        Tag t2 = new Tag("Tag");

        assertThat(t1.equals(t2)).isFalse();
    }

    @Override
    public Arbitrary<? extends Tag> datapoints() {
        Tag tag = new Tag("tag");
        return Arbitraries.of(
            null,
            tag,
            tag,
            new Tag("tag"),
            new Tag(null),
            new Tag("tag2")
        );
    }
}
