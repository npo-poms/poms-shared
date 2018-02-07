/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.util.SortedSet;

import org.junit.Test;

import nl.vpro.domain.media.exceptions.CircularReferenceException;

import static org.assertj.core.api.Assertions.assertThat;

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

        program.createEpisodeOf(group1, 1);
        program.createMemberOf(group2, 1);
        group1.createMemberOf(root, 1);
        group2.createMemberOf(root, 2);

        SortedSet<MediaObject> ancestors = program.getAncestors();

        assertThat(ancestors).hasSize(3);
    }

    @Test(expected = CircularReferenceException.class)
    public void testCreateMemberOfWHenCircular() {
        Program program = new Program(1);
        program.setType(ProgramType.BROADCAST);
        Group group = new Group(2);
        group.setType(GroupType.SERIES);

        program.createEpisodeOf(group, 1);

        assertThat(program.getAncestors()).hasSize(1);

        group.createMemberOf(program, 1);
    }

    @Test(expected = CircularReferenceException.class)
    public void testCreateEpisodeOfWhenCircular() {
        Program program = new Program(1);
        program.setType(ProgramType.BROADCAST);
        Group group = new Group(2);
        group.setType(GroupType.SERIES);

        group.createMemberOf(program, 1);

        assertThat(group.getAncestors()).hasSize(1);

        program.createEpisodeOf(group, 1);
    }
}
