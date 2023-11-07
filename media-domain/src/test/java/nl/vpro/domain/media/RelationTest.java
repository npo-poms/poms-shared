/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.xml.bind.util.JAXBSource;

import org.junit.jupiter.api.Test;
import org.meeuw.theories.ComparableTheory;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;

import static nl.vpro.domain.media.MediaDomainTestHelper.*;
import static nl.vpro.domain.media.update.Validation.getValidator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RelationTest implements ComparableTheory<Relation> {

    @Test
    public void testGetUrnOnNull() {
        Relation relation = new Relation();
        assertThat(relation.getUrn()).isNull();
    }

    @Test
    public void testGetUrnFormat() {
        Relation relation = new Relation();
        relation.setId(1L);
        assertThat(relation.getUrn()).isEqualTo("urn:vpro:media:relation:1");
    }

    @Test
    public void testSetUrn() {
        Relation relation = new Relation();
        relation.setUrn("urn:vpro:media:relation:79");

        assertThat(relation.getId().intValue()).isEqualTo(79);
    }

    @Test
    public void testSetUrnWithoutAnId() {
        assertThatThrownBy(() -> {

            Relation relation = new Relation();
            relation.setUrn("urn:vpro:media:relation:");
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testSetUrnFormat() {
        assertThatThrownBy(() -> {
            Relation relation = new Relation();
            relation.setUrn("vpro:media:relation:79");
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testDefinitionValidation() {
        Relation r = new Relation();
        Set<ConstraintViolation<Relation>> constraintViolations = getValidator().validate(r);

        assertThat(constraintViolations).hasSize(1);
    }

    @Test
    public void testURIValidation() {
        Relation r = new Relation(getDefinition());
        r.setUriRef(":");
        Set<ConstraintViolation<Relation>> constraintViolations = getValidator().validate(r);

        assertThat(constraintViolations).hasSize(1);
    }

    @Test
    public void testUpdateOnDifferentDefinitions() {
        assertThatThrownBy(() -> {

            Relation to = new Relation(new RelationDefinition());
            Relation from = new Relation(new RelationDefinition("GIDS", "BNN"));
            Relation.update(from, to);
        }).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testUpdateOnFieldsCopied() {
        Relation to = new Relation(new RelationDefinition("LABEL", "VPRO"));
        Relation from = new Relation(new RelationDefinition("LABEL", "VPRO"));
        from.setId(1L);
        from.setText("Text");
        from.setUriRef("uri");

        to = Relation.update(from, to);

        assertThat(to.getId()).isNull();
        assertThat(to.getText()).isEqualTo("Text");
        assertThat(to.getUriRef()).isEqualTo("uri");
    }

    @Test
    public void testUpdateOnFieldsNullArgs() {
        Relation to = null;
        Relation from = new Relation(new RelationDefinition());
        to = Relation.update(from, to);
        assertThat(to).isNotNull();

        to = new Relation(new RelationDefinition());
        from = null;

        to = Relation.update(from, to);

        assertThat(to).isNull();
    }

    @Test
    public void testSchemaMapping() throws Exception {
        schemaValidator.validate(new JAXBSource(marshaller, validProgramWithRelation()));
    }

    @Test
    public void json() {
        Jackson2TestUtil.roundTripAndSimilarAndEquals( new Relation(
                56L,
                new RelationDefinition("LABEL", "VPRO", "Record label"),
                "http://sony.com",
                "Sony"
        ), """
            {
              "uriRef" : "http://sony.com",
              "value" : "Sony",
              "type" : "LABEL",
              "urn" : "urn:vpro:media:relation:56",
              "broadcaster" : "VPRO"
            }""");
    }

    private Program validProgramWithRelation() {
        Program program = getXmlValidProgram();

        final Relation relation = new Relation(
            new RelationDefinition("LABEL", "VPRO", "Record label"),
            "http://sony.com",
            "Sony"
        );
        relation.setId(56L);
        program.addRelation(relation);
        return program;
    }

    private RelationDefinition getDefinition() {
        return new RelationDefinition("TYPE", "broadcaster", "text");
    }

    @Override
    public Arbitrary<? extends Relation> datapoints() {
        return Arbitraries.of(
            relation(null, "TYPE", "VPRO"),
            relation(1L, "TYPE", "VPRO"),
            relation(2L, "ANOTHER", "EO"),
            relation(3L, "ANOTHER", "VPRO")
        );
    }

    //@Override
   /* public Arbitrary<? extends Tuple.Tuple2<? extends Relation, ? extends Relation>> equalDatapoints() {
        return Arbitraries.of(
            Tuple.of(relation(null, "TYPE", "VPRO"), relation(null, "TYPE", "VPRO")),
            Tuple.of(relation(2L, "TYPEA", "VPRO"), relation(2L, "TYPE", "VPRO"))
        );
     }
*/

    private static Relation relation(Long id, String type, String broadcaster) {
        Relation relation = new Relation(new RelationDefinition(type, broadcaster));
        relation.setId(id);
        return relation;
    }
}

