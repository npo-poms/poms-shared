/*
 * Copyright (C) 2008 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TitleTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testCompareTo() {
        SortedSet<Title> set = new TreeSet<>();
        set.add(new Title("1", OwnerType.MIS, TextualType.MAIN));
        set.add(new Title("2", OwnerType.MIS, TextualType.EPISODE));
        set.add(new Title("3", OwnerType.MIS, TextualType.ORIGINAL));
        set.add(new Title("4", OwnerType.MIS, TextualType.MAIN));
        set.add(new Title("5", OwnerType.BROADCASTER, TextualType.MAIN));
        set.add(new Title("6", OwnerType.BROADCASTER, TextualType.EPISODE));
        set.add(new Title("7", OwnerType.BROADCASTER, TextualType.ORIGINAL));
        set.add(new Title("8", OwnerType.BROADCASTER, TextualType.ORIGINAL));
        set.add(new Title("9", OwnerType.BROADCASTER, TextualType.SHORT));
        set.add(new Title("10", OwnerType.BROADCASTER, TextualType.SHORT));
        set.add(new Title("11", OwnerType.NEBO, TextualType.SHORT));
        set.add(new Title("12", OwnerType.NEBO, TextualType.EPISODE));
        set.add(new Title("13", OwnerType.NEBO, TextualType.MAIN));

        assertThat(set.first().get()).isEqualTo("5");
        assertThat(set.last().get()).isEqualTo("3");
        assertThat(set).hasSize(10);
    }
}
