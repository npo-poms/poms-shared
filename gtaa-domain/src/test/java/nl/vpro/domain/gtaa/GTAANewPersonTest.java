package nl.vpro.domain.gtaa;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class GTAANewPersonTest {


    @Test
    public void json() {

        GTAANewPerson person = GTAANewPerson.builder().familyName("Puk").givenName("Pietje").scopeNote("test").build();

        Jackson2TestUtil.roundTripAndSimilar(person, "{\n" +
            "  \"newObjectType\" : \"person\",\n" +
            "  \"givenName\" : \"Pietje\",\n" +
            "  \"familyName\" : \"Puk\",\n" +
            "  \"scopeNotes\" : [ \"test\" ]\n" +
            "}");

    }

    @Test
    @Ignore("Will not work. Rest service will support via nl.vpro.jackson2.rs.JsonIdAdderBodyReader")
    public void jsonWithoutType() throws IOException {
        String json = "{\n" +
            "  \"givenName\" : \"Pietje\",\n" +
            "  \"familyName\" : \"Puk\",\n" +
            "  \"scopeNotes\" : [ \"test\" ]\n" +
            "}";
        GTAANewPerson gtaaNewPerson = Jackson2Mapper.getLenientInstance().readValue(json, GTAANewPerson.class);
    }

    @Test
    public void xml() {
        GTAANewPerson person = GTAANewPerson.builder().familyName("Puk").givenName("Pietje").scopeNote("test").build();
        JAXBTestUtil.roundTripAndSimilar(person, "<gtaa:newPerson   xmlns:gtaa=\"urn:vpro:gtaa:2017\" >\n" +
            "    <gtaa:givenName>Pietje</gtaa:givenName>\n" +
            "    <gtaa:familyName>Puk</gtaa:familyName>\n" +
            "    <gtaa:scopeNote>test</gtaa:scopeNote>\n" +
            "</gtaa:newPerson>");

    }
}
