/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import net.jqwik.api.*;

import org.junit.jupiter.api.Test;
import org.meeuw.theories.ComparableTheory;

import static org.assertj.core.api.Assertions.assertThat;

public class TagTest implements ComparableTheory<Tag> {

    @Test
    public void testCaseSensitivity() {
        Tag t1 = new Tag("tag");
        Tag t2 = new Tag("Tag");

        assertThat(t1.equals(t2)).isFalse();
    }

    @Override
    public Arbitrary<Object> datapoints() {
        Tag tag = new Tag("tag");
        return Arbitraries.of(
            tag,
            tag,
            new Tag("tag"),
            new Tag(null),
            new Tag("tag2")
        );
    }

    @Override
    public Arbitrary<Tuple.Tuple2<Object, Object>> equalDatapoints() {
        Tag tagNull = new Tag(null);
        Tag tag1 = new Tag("tag1");
        return Arbitraries.of(
            Tuple.of(new Tag("tag"), new Tag("tag")),
             Tuple.of(tagNull, new Tag(null)),
             Tuple.of(tag1, tag1)
        );
     }
}
