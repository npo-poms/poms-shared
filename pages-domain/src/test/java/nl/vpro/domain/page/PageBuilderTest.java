package nl.vpro.domain.page;


import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.classification.ClassificationService;
import nl.vpro.domain.media.MediaClassificationService;
import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class PageBuilderTest {

    ClassificationService classificationService = MediaClassificationService.getInstance();

    private static final Instant TEST_INSTANT = ZonedDateTime.of(LocalDate.of(2016, 4, 18), LocalTime.NOON, Schedule.ZONE_ID).toInstant();


    @Test
    public void testSortDate() throws Exception {
        Page page = PageBuilder.page(PageType.ARTICLE).publishStart(TEST_INSTANT).build();
        String test = "<local:page publishStart=\"2016-04-18T12:00:00+02:00\" sortDate=\"2016-04-18T12:00:00+02:00\" type=\"ARTICLE\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\"/>\n";
        Page result = JAXBTestUtil.roundTripAndSimilar(page, test);
        assertThat(result.getPublishStartInstant()).isEqualTo(TEST_INSTANT);


    }
    @Test
    public void testSortDateJson() throws Exception {
        Page page = PageBuilder.page(PageType.ARTICLE).publishStart(TEST_INSTANT).build();
        String test = "{\"objectType\":\"page\",\"type\":\"ARTICLE\",\"sortDate\":1460973600000,\"publishStart\":1460973600000}";
        Page result = Jackson2TestUtil.roundTripAndSimilar(page, test);
        assertThat(result.getPublishStartInstant()).isEqualTo(TEST_INSTANT);


    }

    @Test
    public void testCreationDateJson() throws Exception {
        Page page = PageBuilder.page(PageType.ARTICLE).creationDate(TEST_INSTANT).build();
        String test = "{\"objectType\":\"page\",\"type\":\"ARTICLE\",\"sortDate\":1460973600000,\"creationDate\":1460973600000}";
        Page result = Jackson2TestUtil.roundTripAndSimilar(page, test);
        assertThat(result.getCreationDate()).isEqualTo(TEST_INSTANT);


    }


    @Test
    public void testTitle() {
        assertEquals("title", PageBuilder.page(PageType.ARTICLE).title("title").build().getTitle());
    }

    @Test
    public void testParagraph() {
        assertEquals(Arrays.asList(new Paragraph(null, "body", null)), PageBuilder.page(PageType.ARTICLE).paragraphs(new Paragraph(null, "body", null)).build().getParagraphs());
    }

    @Test
    public void testUrl() {
        assertThat(PageBuilder.page(PageType.ARTICLE).url("http://www.vpro.nl").build().getUrl()).isEqualTo("http://www.vpro.nl");
    }

    @Test
    public void testPortal() {
        Portal portal = new Portal("VPRONL", "http://vpro.nl", "title");
        assertThat(PageBuilder.page(PageType.ARTICLE).portal(portal).build().getPortal()).isEqualTo(portal);
    }

    @Test
    public void testMainImage() {
        assertThat(PageBuilder.page(PageType.ARTICLE).image("http://www.vpro.nl/eenofanderplaatje.png").build().getImages().get(0).getUrl()).isEqualTo("http://www.vpro.nl/eenofanderplaatje.png");
    }

    @Test
    public void testSummary() {
        assertEquals("summary", PageBuilder.page(PageType.ARTICLE).summary("summary").build().getSummary());
    }

    @Test
    public void getBroadcasters() {
        assertThat(PageBuilder.page(PageType.ARTICLE).broadcasters(new Broadcaster("VPRO"), new Broadcaster("KRO")).build().getBroadcasters()).containsExactly(new Broadcaster("VPRO"), new Broadcaster("KRO"));
    }

    @Test
    public void testStatRefs() {
        assertEquals(PageBuilder.page(PageType.ARTICLE).statRefs("comscore1", "comscore2").build().getStatRefs(), Arrays.asList("comscore1", "comscore2"));
    }

    @Test
    public void testAlternativeUrls() {
        assertEquals(PageBuilder.page(PageType.ARTICLE).alternativeUrls("http://www.vpro.nl", "http://www.cinema.nl/").build().getAlternativeUrls(), Arrays.asList("http://www.vpro.nl", "http://www.cinema.nl/"));
    }

    @Test
    public void testGenres() throws IOException, SAXException {
        Page page = PageBuilder.page(PageType.ARTICLE).url("http://www.vpro.nl").genres(classificationService.getTerm("3.0.1.1.4")).build();
        String test = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<local:page url=\"http://www.vpro.nl\" type=\"ARTICLE\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:local=\"uri:local\">\n" +
            "    <pages:genre displayName=\"Jeugd - Sport\" id=\"3.0.1.1.4\">\n" +
            "        <pages:term>Jeugd</pages:term>\n" +
            "        <pages:term>Sport</pages:term>\n" +
            "    </pages:genre>\n" +
            "</local:page>";
        Page result = JAXBTestUtil.roundTripAndSimilar(page, test);
        assertThat(result.getGenres()).hasSize(1);
        assertThat(result.getGenres().first().getTermId()).isEqualTo("3.0.1.1.4");
    }

    @Test
    public void testGenresJson() throws IOException {
        Page page = PageBuilder.page(PageType.ARTICLE).url("http://www.vpro.nl").genres(classificationService.getTerm("3.0.1.1.4")).build();
        String result = Jackson2Mapper.getInstance().writer().writeValueAsString(page);
        Jackson2TestUtil.assertThatJson(result).isSimilarTo("{\"objectType\":\"page\",\"type\":\"ARTICLE\",\"url\":\"http://www.vpro.nl\",\"genres\":[{\"id\":\"3.0.1.1.4\",\"terms\":[\"Jeugd\",\"Sport\"],\"displayName\":\"Jeugd - Sport\"}]}");
    }



    @Test
    public void testLastPublished() throws IOException, SAXException {
        Page page = PageBuilder.page(PageType.ARTICLE).lastPublished(Instant.EPOCH).build();
        String test = "<local:page xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\" lastPublished=\"1970-01-01T01:00:00+01:00\" type=\"ARTICLE\"/>";
        Page result = JAXBTestUtil.roundTripAndSimilar(page, test);
        assertThat(result.getLastPublished()).isEqualTo(Instant.EPOCH);
    }

    @Test
    public void testLastPublishedJson() throws Exception {
        Page page = PageBuilder.page(PageType.ARTICLE).lastPublished(Instant.EPOCH).build();
        String test = "{\"objectType\":\"page\",\"type\":\"ARTICLE\",\"lastPublished\":0}";
        Page result = Jackson2TestUtil.roundTripAndSimilar(page, test);
        assertThat(result.getLastPublished()).isEqualTo(Instant.EPOCH);
    }

}
