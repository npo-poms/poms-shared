/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;

import nl.vpro.test.theory.ObjectTest;

import static org.assertj.core.api.Assertions.assertThat;

public class RelationDefinitionTest extends ObjectTest<RelationDefinition> {

    @DataPoint
    public static RelationDefinition nullArgument = null;

    @DataPoint
    public static RelationDefinition withEmptyFields = new RelationDefinition();

    @DataPoint
    public static RelationDefinition idOnly = new RelationDefinition("LABEL", "VPRO");

    @DataPoint
    public static RelationDefinition idWithText = new RelationDefinition("LABEL", "VPRO", "Added text");

    private static Validator validator;

    @BeforeClass
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
}
