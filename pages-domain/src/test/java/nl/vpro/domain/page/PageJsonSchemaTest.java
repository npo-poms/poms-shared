package nl.vpro.domain.page;

import java.io.IOException;
import java.time.*;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.classification.ClassificationService;
import nl.vpro.domain.classification.ClassificationServiceImpl;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class PageJsonSchemaTest {

    private PageBuilder builder;

    @BeforeEach
    public void setUp() {
        builder = PageBuilder.page(PageType.ARTICLE);
    }

    @Test
    public void testBroadcasters() throws Exception {
        Page news = builder.broadcasters(new Broadcaster("VPRO", "VPRO")).build();

        news = roundTripContains(news, "\"broadcasters\":[{\"id\":\"VPRO\",\"value\":\"VPRO\"}]}");

        assertThat(news.getBroadcasters()).hasSize(1);
    }

    @Test
    public void testCrids() throws Exception {
        Page news = builder.crids("crid://vpro.nl/article/12345").build();

        news = roundTripContains(news, "\"crids\":[\"crid://vpro.nl/article/12345\"]}");

        assertThat(news.getCrids()).hasSize(1);
        assertThat(news.getCrids().get(0)).isEqualTo("crid://vpro.nl/article/12345");
    }

    @Test
    public void testPortalWithSection() {
        Portal portal = new Portal("VPRONL", "http://vpro.nl", "VPRO");
        Section section = new Section("/tegenlicht", "Tegenlicht");
        portal.setSection(section);
        Page news = builder.portal(portal).build();

        news = Jackson2TestUtil.roundTripAndSimilarAndEquals(news, "{\n" +
            "  \"objectType\" : \"page\",\n" +
            "  \"type\" : \"ARTICLE\",\n" +
            "  \"portal\" : {\n" +
            "    \"id\" : \"VPRONL\",\n" +
            "    \"url\" : \"http://vpro.nl\",\n" +
            "    \"value\" : \"VPRO\",\n" +
            "    \"section\" : {\n" +
            "      \"path\" : \"/tegenlicht\",\n" +
            "      \"id\" : \"VPRONL./tegenlicht\",\n" +
            "      \"value\" : \"Tegenlicht\"\n" +
            "    }\n" +
            "  }\n" +
            "}");

        assertThat(news.getPortal()).isNotNull();
        assertThat(news.getPortal().getSection()).isNotNull();
    }

    @Test
    public void testParagraphs() throws Exception {
        Page news = builder.paragraphs(new Paragraph("Title", "Description", new Image("http://image.domain", "Subscript"))).build();

        news = roundTripContains(news, "\"paragraphs\":[{\"title\":\"Title\",\"body\":\"Description\",\"image\":{\"url\":\"http://image.domain\",\"title\":\"Subscript\"}}]");

        assertThat(news.getParagraphs()).hasSize(1);
        assertThat(news.getParagraphs().get(0).getImage()).isNotNull();
    }

    @Test
    public void testEmbeds() throws Exception {
        Page news = builder.embeds(new Embed(new Program(), "Title", "Description")).build();

        news = roundTripContains(news, "\"embeds\":[{\"title\":\"Title\",\"description\":\"Description\",\"media\":{\"objectType\":\"program\"");

        assertThat(news.getEmbeds()).hasSize(1);
        assertThat(news.getEmbeds().get(0).getMedia()).isNotNull();
    }

    @Test
    public void testImages() throws Exception {
        Page news = builder.image("http://images.poms.omroep.nl/1234").build();

        news = roundTripContains(news, "\"images\":[{\"url\":\"http://images.poms.omroep.nl/1234\"}]");

        assertThat(news.getImages()).hasSize(1);
        assertThat(news.getImages().get(0).getUrl()).isEqualTo("http://images.poms.omroep.nl/1234");
    }


    @Test
    public void testGenres() throws Exception {
        ClassificationService classificationService = ClassificationServiceImpl.fromClassPath("nl/vpro/domain/media/classification/ebu_ContentGenreCS.xml");
        Page news = builder.genres(classificationService.getTerm("3.0.1.1.11")).build();

        news = roundTripContains(news, "\"genres\":[{\"id\":\"3.0.1.1.11\",\"terms\":[\"Jeugd\",\"Animatie\"],\"displayName\":\"Jeugd - Animatie\"}]");

        assertThat(news.getGenres()).hasSize(1);
        assertThat(news.getGenres().iterator().next().getTermId()).isEqualTo("3.0.1.1.11");
    }


    @Test
    public void testRelations() {
        Page news = builder.relationText(RelationDefinition.of("CINEMA_DIRECTOR", "VPRO"), "Stanley Kubrick").build();

        news = Jackson2TestUtil.roundTripAndSimilar(news, "{\"objectType\":\"page\",\"type\":\"ARTICLE\",\"relations\":[{\"value\":\"Stanley Kubrick\",\"type\":\"CINEMA_DIRECTOR\",\"broadcaster\":\"VPRO\"}]}");

        assertThat(news.getRelations()).hasSize(1);
        assertThat(news.getRelations().iterator().next().getBroadcaster()).isEqualTo("VPRO");
    }

    @Test
    public void testDateFields() {
        Instant date = LocalDateTime.of(LocalDate.of(2016, 4, 25), LocalTime.NOON).atZone(Schedule.ZONE_ID).toInstant();
        Page news = builder.creationDate(date).lastModified(date.plus(5, ChronoUnit.MINUTES)).lastPublished(date.plus(10, ChronoUnit.MINUTES)).build();
        news = Jackson2TestUtil.roundTripAndSimilar(news, "{\"objectType\":\"page\",\"type\":\"ARTICLE\",\"sortDate\":1461578400000,\"creationDate\":1461578400000,\"lastModified\":1461578700000,\"lastPublished\":1461579000000}");

        assertThat(news.getCreationDate()).isEqualTo(date);
        assertThat(news.getLastModified()).isEqualTo(date.plus(5, ChronoUnit.MINUTES));
        assertThat(news.getLastPublished()).isEqualTo(date.plus(10, ChronoUnit.MINUTES));
    }




    private Page roundTripContains(Page page, String content) throws IOException {
        String json = Jackson2Mapper.getInstance().writeValueAsString(page);
        assertThat(json).contains(content);
        return Jackson2Mapper.getInstance().readerFor(Page.class).readValue(json);
    }
}
