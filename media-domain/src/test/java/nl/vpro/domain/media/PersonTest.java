package nl.vpro.domain.media;

import org.junit.Test;

import nl.vpro.beeldengeluid.gtaa.GTAARecord;
import nl.vpro.beeldengeluid.gtaa.Status;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public class PersonTest {


    @Test
    public void json() throws Exception {
        Person person = new Person("Pietje", "Puk", RoleType.ACTOR);
        person.setGtaaRecord(new GTAARecord("http://data.beeldengeluid.nl/gtaa/1869521", Status.approved));
        Jackson2TestUtil.roundTripAndSimilar(person, "{\n" +
            "  \"givenName\" : \"Pietje\",\n" +
            "  \"familyName\" : \"Puk\",\n" +
            "  \"role\" : \"ACTOR\",\n" +
            "  \"gtaaUri\" : \"http://data.beeldengeluid.nl/gtaa/1869521\"\n" +
            "}");

    }


    @Test
    public void xml() throws Exception {
        Person person = new Person("Pietje", "Puk", RoleType.ACTOR);
        person.setGtaaRecord(new GTAARecord("http://data.beeldengeluid.nl/gtaa/1869521", Status.approved));
        JAXBTestUtil.roundTripAndSimilar(person, "<local:person role=\"ACTOR\" gtaaUri=\"http://data.beeldengeluid.nl/gtaa/1869521\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:local=\"uri:local\">\n" +
            "    <givenName>Pietje</givenName>\n" +
            "    <familyName>Puk</familyName>\n" +
            "</local:person>");

    }

}
