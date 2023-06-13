package nl.vpro.domain.page.update;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.Schedule;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public class PageUpdateChangeTest {


    @Test
    public void xml() {
        PageUpdateChange change =
            PageUpdateChange.builder()
                .id("http://www.vpro.nl/pagina/1")
                .deleted(false)
                .publishDate(LocalDateTime.of(2017, 1, 30, 11, 41).atZone(Schedule.ZONE_ID).toInstant())
                .object(PageUpdateBuilder.article("http://www.vpro.nl/pagina/1").build())
                .build();

        JAXBTestUtil.assertThatXml(change).isSimilarTo("""
            <pageUpdate:pageUpdateChange publishDate="2017-01-30T11:41:00+01:00" id="http://www.vpro.nl/pagina/1" deleted="false" xmlns:shared="urn:vpro:shared:2009" xmlns:page="urn:vpro:pages:2013" xmlns:media="urn:vpro:media:2009" xmlns:pageUpdate="urn:vpro:pages:update:2013">
                <pageUpdate:object type="ARTICLE" url="http://www.vpro.nl/pagina/1"/>
            </pageUpdate:pageUpdateChange>
            """);
    }


    @Test
    public void json() {
        PageUpdateChange change =
            PageUpdateChange.builder()
                .id("http://www.vpro.nl/pagina/1")
                .deleted(false)
                .publishDate(LocalDateTime.of(2017, 1, 30, 11, 41).atZone(Schedule.ZONE_ID).toInstant())
                .object(PageUpdateBuilder.article("http://www.vpro.nl/pagina/1").build())
                .build();

        Jackson2TestUtil.assertThatJson(change).isSimilarTo("""
            {
              "publishDate" : 1485772860000,
              "id" : "http://www.vpro.nl/pagina/1",
              "deleted" : false,
              "object" : {
                "type" : "ARTICLE",
                "url" : "http://www.vpro.nl/pagina/1"
              }
            }""");

    }

}
