/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import net.jqwik.api.*;

import java.util.Set;

import javax.validation.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.meeuw.util.test.BasicObjectTheory;

import static org.assertj.core.api.Assertions.assertThat;

public class RelationDefinitionTest implements BasicObjectTheory<RelationDefinition> {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
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
            new RelationDefinition(), // with empty fields
            new RelationDefinition("LABEL", "VPRO"), // ide only
            new RelationDefinition("LABEL", "VPRO", "Added text"));
    }

   @Override
   public Arbitrary<? extends Tuple.Tuple2<? extends RelationDefinition, ? extends RelationDefinition>> equalDatapoints() {
        return Arbitraries.of(
            Tuple.of(new RelationDefinition(), new RelationDefinition()),
            Tuple.of(
                new RelationDefinition("LABEL", "VPRO"),
                new RelationDefinition("LABEL", "VPRO", "Added text"))
        );
    }
}
