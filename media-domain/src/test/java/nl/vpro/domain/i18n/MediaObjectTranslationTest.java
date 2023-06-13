package nl.vpro.domain.i18n;

import java.time.Instant;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public class MediaObjectTranslationTest {

    @Test
    public void xml() {
        MediaObjectTranslation trans = new MediaObjectTranslation("MID_1234", Locale.ENGLISH);
        trans.setCreationInstant(Instant.EPOCH);
        trans.setMainTitle("bla bla");

        JAXBTestUtil.roundTripAndSimilar(trans, """
            <mediai18n:media creationDate="1970-01-01T01:00:00+01:00" mid="MID_1234" xml:lang="en" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:mediai18n="urn:vpro:media:i18n:2017">
                <mediai18n:title owner="BROADCASTER" type="MAIN">bla bla</mediai18n:title>
            </mediai18n:media>""");
    }


    @Test
    public void json() {
        MediaObjectTranslation trans = new MediaObjectTranslation("MID_1234", Locale.ENGLISH);
        trans.setCreationInstant(Instant.EPOCH);
        trans.setMainTitle("bla bla");

        Jackson2TestUtil.roundTripAndSimilar(trans, """
            {
              "mid" : "MID_1234",
              "creationDate" : 0,
              "lang" : "en",
              "titles" : [ {
                "owner" : "BROADCASTER",
                "type" : "MAIN",
                "value" : "bla bla"
              } ]
            }""");
    }

}
