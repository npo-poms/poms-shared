package nl.vpro.domain.api.thesaurus;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import nl.vpro.domain.gtaa.GTAAPerson;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class PersonResultTest {

    PersonResult result;
    {
        GTAAPerson person = new GTAAPerson();
        person.setGivenName("Pietje");
        person.setFamilyName("Puk");
        List<GTAAPerson> list = new ArrayList<>();
        list.add(person);

        result =
            PersonResult.builder()
                .list(list)
                .max(10)
                .build();

    }


    @Test
    public void toJson() {

        Jackson2TestUtil.roundTripAndSimilarAndEquals(result, "{\n" +
            "  \"offset\" : 0,\n" +
            "  \"max\" : 10,\n" +
            "  \"items\" : [ {\n" +
            "    \"objectType\" : \"person\",\n" +
            "    \"givenName\" : \"Pietje\",\n" +
            "    \"familyName\" : \"Puk\",\n" +
            "    \"name\" : \"Puk, Pietje\"\n" +
            "  } ]\n" +
            "}");

    }


    @Test
    public void toXml() {
        // TODO: It seems silly to have a dedicated result type, but then still have to rely on xsi:type

        JAXBTestUtil.roundTripAndSimilarAndEquals(result, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<api:personResult offset=\"0\" max=\"10\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:skosxl=\"http://www.w3.org/2008/05/skos-xl#\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:media=\"urn:vpro:media:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:oai=\"http://www.openarchives.org/OAI/2.0/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:api=\"urn:vpro:api:2013\" xmlns:gtaa=\"urn:vpro:gtaa:2017\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:openskos=\"http://openskos.org/xmlns#\">\n" +
            "    <api:items>\n" +
            "        <api:item xsi:type=\"gtaa:personType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "            <gtaa:name>Puk, Pietje</gtaa:name>\n" +
            "            <gtaa:givenName>Pietje</gtaa:givenName>\n" +
            "            <gtaa:familyName>Puk</gtaa:familyName>\n" +
            "        </api:item>\n" +
            "    </api:items>\n" +
            "</api:personResult>");

    }

}
