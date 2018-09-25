package nl.vpro.rs.thesaurus.update;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXB;

import nl.vpro.domain.media.gtaa.GTAANewPerson;
import org.junit.Ignore;
import org.junit.Test;

import nl.vpro.logging.LoggerOutputStream;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
@Ignore("Person Request is deleted. So will this test..")
public class GTAANewPersonRequestTest {


    @Test
    public void json() throws Exception {

        GTAANewPerson person = GTAANewPerson.builder().familyName("Puk").givenName("Pietje").note("test").build();


        Jackson2TestUtil.roundTripAndSimilar(person, "{\n" +
            "  \"person\" : {\n" +
            "    \"givenName\" : \"Pietje\",\n" +
            "    \"familyName\" : \"Puk\",\n" +
            "    \"note\" : \"test\"\n" +
            "  }\n" +
            "}");

    }


    @Test
    public void xml() throws Exception {

        GTAANewPerson person = GTAANewPerson.builder().familyName("Puk").givenName("Pietje").note("test").build();

        JAXBTestUtil.roundTripAndSimilar(person, "<gtaa:newPersonRequest xmlns:gtaa=\"urn:vpro:gtaa:2017\">\n" +
            "    <gtaa:person>\n" +
            "        <gtaa:familyName>Puk</gtaa:familyName>\n" +
            "        <gtaa:givenName>Pietje</gtaa:givenName>\n" +
            "        <gtaa:note>test</gtaa:note>\n" +
            "    </gtaa:person>\n" +
            "</gtaa:newPersonRequest>");

        JAXB.marshal(person, LoggerOutputStream.info(log));

    }

}
