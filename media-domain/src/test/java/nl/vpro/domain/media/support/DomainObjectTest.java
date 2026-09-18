/*
 * Copyright (C) 2008 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import nl.vpro.domain.media.Group;
import nl.vpro.domain.media.Program;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class DomainObjectTest {

    @Test
    void equalsForRealIdentity() {
        Program program = new Program();
        assertThat(program).isEqualTo(program);
    }

    @Test
    void equalsWithIdenticalIDs() {
        Program program = new Program(123L);
        Program program2 = new Program(123L);
        assertThat(program).isEqualTo(program2);
    }

    @Test
    void equalsWithNoIdenticalIDs() {
        Program program = new Program(123L);
        Program program2 = new Program(124L);
        assertThat(program).isNotEqualTo(program2);
    }

    @Test
    void equalsWithNullIDs() {
        Program program = new Program();
        Program program2 = new Program();
        assertThat(program).isNotEqualTo(program2);
    }

    @Test
    void equalsWithNull() {
        Program program = new Program();
        assertThat(program).isNotEqualTo(null);
    }

    @Test
    void equalsWithOtherDomainObject() {
        Program program = new Program();
        Group group = new Group();
        assertThat(program).isNotEqualTo(group);
    }

    @Test
    void equalsWithOtherObject() {
        Program program = new Program();
        String string = "";
        assertThat(program).isNotEqualTo(string);
    }
}
