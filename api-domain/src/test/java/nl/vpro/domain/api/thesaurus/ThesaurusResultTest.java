package nl.vpro.domain.api.thesaurus;

import nl.vpro.domain.media.gtaa.GTAAPerson;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ThesaurusResultTest {

    @Test
    public void toJson() throws Exception {
        GTAAPerson person = new GTAAPerson();
        person.setGivenName("Pietje");
        person.setFamilyName("Puk");
        List<GTAAPerson> list = new ArrayList<>();
        list.add(person);

        ThesaurusResult.PersonList result =
                ThesaurusResult.PersonList.builder()
                        .list(list)
                        .max(10)
                        .build();

        Jackson2TestUtil.roundTripAndSimilarAndEquals(result, "{\n" +
                "  \"offset\" : 0,\n" +
                "  \"max\" : 10,\n" +
                "  \"items\" : [ {\n" +
                "    \"objectType\" : \"person\",\n" +
                "    \"givenName\" : \"Pietje\",\n" +
                "    \"familyName\" : \"Puk\",\n" +
                "    \"value\" : \"Pietje Puk\",\n" +
                "    \"prefLabel\" : \"Pietje Puk\"\n" +
                "  } ]\n" +
                "}");

    }





}



