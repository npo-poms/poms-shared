package nl.vpro.rs.thesaurus.update;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXB;

import org.junit.Test;

import nl.vpro.logging.LoggerOutputStream;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
public class NewPersonRequestTest {


    @Test
    public void json() throws Exception {

        NewPersonRequest person =
            NewPersonRequest.builder()
                .person(NewPerson.builder().familyName("Puk").givenName("Pietje").note("test").build())
                .jws("abc").build();

        Jackson2TestUtil.roundTripAndSimilar(person, "{\n" +
            "  \"person\" : {\n" +
            "    \"givenName\" : \"Pietje\",\n" +
            "    \"familyName\" : \"Puk\",\n" +
            "    \"note\" : \"test\"\n" +
            "  },\n" +
            "  \"jws\" : \"abc\"\n" +
            "}");

    }


    @Test
    public void xml() throws Exception {
        NewPersonRequest person =
            NewPersonRequest.builder()
                .person(NewPerson.builder().familyName("Puk").givenName("Pietje").note("test").build())
                .jws("abc").build();
        JAXBTestUtil.roundTripAndSimilar(person, "<gtaa:newPersonRequest xmlns:gtaa=\"urn:vpro:gtaa:2017\">\n" +
            "    <gtaa:jws>abc</gtaa:jws>\n" +
            "    <gtaa:person>\n" +
            "        <gtaa:familyName>Puk</gtaa:familyName>\n" +
            "        <gtaa:givenName>Pietje</gtaa:givenName>\n" +
            "        <gtaa:note>test</gtaa:note>\n" +
            "    </gtaa:person>\n" +
            "</gtaa:newPersonRequest>");

        JAXB.marshal(person, LoggerOutputStream.info(log));

    }

}
