/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import java.util.Set;

import javax.validation.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nl.vpro.test.jqwik.BasicObjectTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RelationDefinitionTest implements BasicObjectTest<RelationDefinition> {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValid() {
        RelationDefinition definition = new RelationDefinition("ABC1", "BROADCASTER", "Tekst");

        Set<ConstraintViolation<RelationDefinition>> constraintViolations =
            validator.validate(definition);

        assertThat(constraintViolations).isEmpty();
    }

    @Test
    public void testSetTypeToShort() {
        RelationDefinition definition = new RelationDefinition("ABC", "BROADCASTER", "Tekst");

        Set<ConstraintViolation<RelationDefinition>> constraintViolations =
            validator.validate(definition);

        assertThat(constraintViolations).hasSize(1);
    }

    @Test
    public void testSetTypeIllegalCharacter() {
        RelationDefinition definition = new RelationDefinition("ABC@D", "BROADCASTER", "Tekst");

        Set<ConstraintViolation<RelationDefinition>> constraintViolations =
            validator.validate(definition);

        assertThat(constraintViolations).hasSize(1);
    }

    @Test
    public void testSetBroadcasterWithEmptyValue() {
        RelationDefinition definition = new RelationDefinition("ABC1", "", "Tekst");

        Set<ConstraintViolation<RelationDefinition>> constraintViolations = validator.validate(definition);

        assertThat(constraintViolations).hasSize(1);
    }

    @Override
    public Arbitrary<? extends RelationDefinition> datapoints() {
        return Arbitraries.of(
            null,
            new RelationDefinition(), // with empty fields
            new RelationDefinition("LABEL", "VPRO"), // ide only
            new RelationDefinition("LABEL", "VPRO", "Added text"));
    }
}
