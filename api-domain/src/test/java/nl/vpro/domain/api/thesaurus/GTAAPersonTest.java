package nl.vpro.domain.api.thesaurus;

import java.io.StringReader;

import org.junit.Test;

import nl.vpro.domain.media.gtaa.GTAAPerson;
import nl.vpro.domain.media.gtaa.ThesaurusItem;
import nl.vpro.domain.media.gtaa.ThesaurusObject;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class GTAAPersonTest {
    @Test
    public void json() throws Exception {
        GTAAPerson person = GTAAPerson.builder().givenName("Pietje").familyName("puk").build();

        Jackson2TestUtil.roundTripAndSimilarAndEquals(person, "{\n" +
            "  \"objectType\" : \"person\",\n" +
            "  \"givenName\" : \"Pietje\",\n" +
            "  \"familyName\" : \"puk\",\n" +
            "}");

    }

    @Test
    public void jsonAsThesaurusObjectReturnsPerson() throws Exception {


        ThesaurusObject object = Jackson2Mapper.getInstance().readValue(new StringReader("{\n" +
            "  \"objectType\" : \"person\",\n" +
            "  \"familyName\" : \"puk\",\n" +
            "  \"value\" : \"null puk\",\n" +
            "  \"prefLabel\" : \"null puk\"\n" +
            "}"), ThesaurusObject.class);

        assertThat(object).isInstanceOf(GTAAPerson.class);

    }

    @Test
    public void jsonAsThesaurusObjectReturnsItem() throws Exception {


        ThesaurusObject object = Jackson2Mapper.getInstance().readValue(new StringReader("{\n" +
            "  \"objectType\" : \"item\",\n" +
            "  \"prefLabel\" : \"augustijnen\"\n" +
            "}"), ThesaurusObject.class);

        assertThat(object).isInstanceOf(ThesaurusItem.class);

    }

    @Test
    public void xml() throws Exception {
        GTAAPerson person = GTAAPerson.builder().familyName("puk").build();

        JAXBTestUtil.roundTripAndSimilarAndEquals(person, "<a />");

    }
}
