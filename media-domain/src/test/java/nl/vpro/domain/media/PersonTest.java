package nl.vpro.domain.media;

import org.junit.Test;

import nl.vpro.domain.gtaa.persistence.EmbeddablePerson;
import nl.vpro.domain.gtaa.Status;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static nl.vpro.test.util.jackson2.Jackson2TestUtil.assertThatJson;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public class PersonTest {


    @Test
    public void json() {
        Person person = new Person("Pietje", "Puk", RoleType.ACTOR);
        person.setGtaaRecord(new EmbeddablePerson("http://data.beeldengeluid.nl/gtaa/1869521", Status.approved));
        assertThatJson(person).isSimilarTo(
            "{\n" +
            "  \"givenName\" : \"Pietje\",\n" +
            "  \"familyName\" : \"Puk\",\n" +
            "  \"role\" : \"ACTOR\",\n" +
            "  \"gtaaUri\" : \"http://data.beeldengeluid.nl/gtaa/1869521\"\n" +
            "}").andRounded().isEqualTo(person);

    }


    @Test
    public void xml() {
        Person person = new Person("Pietje", "Puk", RoleType.ACTOR);
        person.setGtaaRecord(new EmbeddablePerson("http://data.beeldengeluid.nl/gtaa/1869521", Status.approved));
        JAXBTestUtil.assertThatXml(person).isSimilarTo(
            "<local:person role=\"ACTOR\" gtaaUri=\"http://data.beeldengeluid.nl/gtaa/1869521\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:local=\"uri:local\">\n" +
            "    <givenName>Pietje</givenName>\n" +
            "    <familyName>Puk</familyName>\n" +
            "</local:person>").andRounded().isEqualTo(person);

    }

}
