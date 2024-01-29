package nl.vpro.domain.gtaa;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import nl.vpro.jackson2.rs.JsonIdAdderBodyReader;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class GTAANewPersonTest {
    JsonIdAdderBodyReader reader = new JsonIdAdderBodyReader();



    @Test
    public void json() {

        GTAANewPerson person = GTAANewPerson.builder().familyName("Puk").givenName("Pietje").scopeNote("test").build();

        Jackson2TestUtil.roundTripAndSimilar(person, """
            {
              "newObjectType" : "person",
              "givenName" : "Pietje",
              "familyName" : "Puk",
              "scopeNotes" : [ "test" ]
            }""");

    }

    @Test
    public void jsonWithoutType() throws IOException {
        String json = """
            {
              "givenName" : "Pietje",
              "familyName" : "Puk",
              "scopeNotes" : [ "test" ]
            }""";

        GTAANewPerson gtaaNewPerson =
            (GTAANewPerson) reader.readFrom(Object.class,
                GTAANewPerson.class,
                null,
                APPLICATION_JSON_TYPE, null, new ByteArrayInputStream(json.getBytes()));

        assertThat(gtaaNewPerson.getGivenName()).isEqualTo("Pietje");
    }

    @Test
    public void xml() {
        GTAANewPerson person = GTAANewPerson.builder().familyName("Puk").givenName("Pietje").scopeNote("test").build();
        JAXBTestUtil.roundTripAndSimilar(person, """
            <gtaa:newPerson   xmlns:gtaa="urn:vpro:gtaa:2017" >
                <gtaa:givenName>Pietje</gtaa:givenName>
                <gtaa:familyName>Puk</gtaa:familyName>
                <gtaa:scopeNote>test</gtaa:scopeNote>
            </gtaa:newPerson>""");

    }
}
