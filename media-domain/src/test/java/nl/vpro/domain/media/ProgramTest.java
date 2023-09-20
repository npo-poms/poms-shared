/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.util.SortedSet;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.exceptions.CircularReferenceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProgramTest {

    @Test
    public void testGetAncestors() throws CircularReferenceException {
        Program program = new Program(1);
        program.setType(ProgramType.BROADCAST);
        Group group1 = new Group(2);
        group1.setType(GroupType.SERIES);
        Group group2 = new Group(3);
        group2.setType(GroupType.PLAYLIST);
        Group root = new Group(4);
        root.setType(GroupType.PLAYLIST);

        program.createEpisodeOf(group1, 1, null);
        program.createMemberOf(group2, 1, null);
        group1.createMemberOf(root, 1, null);
        group2.createMemberOf(root, 2, null);

        SortedSet<MediaObject> ancestors = program.getAncestors();

        assertThat(ancestors).hasSize(3);
    }

    @Test
    public void testCreateMemberOfWHenCircular() {
        assertThatThrownBy(() -> {
            Program program = new Program(1);
            program.setType(ProgramType.BROADCAST);
            Group group = new Group(2);
            group.setType(GroupType.SERIES);

            program.createEpisodeOf(group, 1, null);

            assertThat(program.getAncestors()).hasSize(1);

            group.createMemberOf(program, 1, null);
        }).isInstanceOf(CircularReferenceException.class);
    }

    @Test
    public void testCreateEpisodeOfWhenCircular() {
        assertThatThrownBy(() -> {

            Program program = new Program(1);
            program.setType(ProgramType.BROADCAST);
            Group group = new Group(2);
            group.setType(GroupType.SERIES);

            group.createMemberOf(program, 1, null);

            assertThat(group.getAncestors()).hasSize(1);

            program.createEpisodeOf(group, 1, null);
        }).isInstanceOf(CircularReferenceException.class);
    }
}
