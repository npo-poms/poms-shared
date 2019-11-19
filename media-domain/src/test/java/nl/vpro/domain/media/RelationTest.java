/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.xml.bind.util.JAXBSource;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.theory.ComparableTest;

import static nl.vpro.domain.media.MediaDomainTestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

public class RelationTest extends ComparableTest<Relation> {

    @DataPoint
    public static Relation nullArgument = null;

    @DataPoint
    public static Relation withDefinition = relation(null, "TYPE", "VPRO");

    @DataPoint
    public static Relation persisted = relation(1L, "TYPE", "VPRO");

    private static Relation relation(Long id, String type, String broadcaster) {
        Relation relation = new Relation(new RelationDefinition(type, broadcaster));
        relation.setId(id);
        return relation;
    }

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

    @Test(expected = IllegalArgumentException.class)
    public void testSetUrnWithoutAnId() {
        Relation relation = new Relation();
        relation.setUrn("urn:vpro:media:relation:");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetUrnFormat() {
        Relation relation = new Relation();
        relation.setUrn("vpro:media:relation:79");
    }

    @Test
    public void testDefinitionValidation() {
        Relation r = new Relation();
        Set<ConstraintViolation<Relation>> constraintViolations = validator.validate(r);

        assertThat(constraintViolations).hasSize(1);
    }

    @Test
    public void testURIValidation() {
        Relation r = new Relation(getDefinition());
        r.setUriRef(":");
        Set<ConstraintViolation<Relation>> constraintViolations = validator.validate(r);

        assertThat(constraintViolations).hasSize(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUpdateOnDifferentDefinitions() {
        Relation to = new Relation(new RelationDefinition());
        Relation from = new Relation(new RelationDefinition("GIDS", "BNN"));
        Relation.update(from, to);
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
        ), "{\n" +
                "  \"uriRef\" : \"http://sony.com\",\n" +
                "  \"value\" : \"Sony\",\n" +
                "  \"type\" : \"LABEL\",\n" +
                "  \"urn\" : \"urn:vpro:media:relation:56\",\n" +
                "  \"broadcaster\" : \"VPRO\"\n" +
                "}");
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
}
