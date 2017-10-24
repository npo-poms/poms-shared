package nl.vpro.rs.thesaurus.update;

import org.junit.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class NewPersonTest {


    @Test
    public void json() throws Exception {

        NewPerson person = NewPerson.builder().familyName("Puk").givenName("Pietje").note("test").build();

        Jackson2TestUtil.roundTripAndSimilar(person, "{\n" +
            "  \"givenName\" : \"Pietje\",\n" +
            "  \"familyName\" : \"Puk\",\n" +
            "  \"note\" : \"test\"\n" +
            "}");

    }


    @Test
    public void xml() throws Exception {
        NewPerson person = NewPerson.builder().familyName("Puk").givenName("Pietje").note("test").build();
        JAXBTestUtil.roundTripAndSimilar(person, "<gtaa:newPerson xmlns:gtaa=\"urn:vpro:gtaa:2017\">\n" +
            "    <gtaa:familyName>Puk</gtaa:familyName>\n" +
            "    <gtaa:givenName>Pietje</gtaa:givenName>\n" +
            "    <gtaa:note>test</gtaa:note>\n" +
            "</gtaa:newPerson>");

    }
}
