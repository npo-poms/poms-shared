package nl.vpro.domain.page;

import java.time.LocalDateTime;

import javax.xml.transform.stream.StreamSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.*;
import nl.vpro.domain.support.License;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 */
public class PageTest {

    @BeforeAll
    public static void staticInit() {
        ClassificationServiceLocator.setInstance(MediaClassificationService.getInstance());
    }

    Page page = Page.builder()
        .url("http://www.npo.nl/1")
        .alternativeUrls("http://npo.nl/1")
        .broadcasters("VPRO", "HUMAN")
        .crids("crid://npo/1")
        .keywords("foo", "bar")
        .title("title")
        .subtitle("sub title")
        .type(PageType.ARTICLE)
        .genres(ClassificationServiceLocator.getInstance().getTerm("3.0.1.7.21"))
        .portal(new Portal("3VOOR12", "https://3voor12.vpro.nl", "3 voor 12"))
        .paragraphs(new Paragraph("ptitle", "pbody",
            Image.builder()
            .url("https://www.vpro.nl/foobar.png")
            .title( "an image")
            .license(License.CC_BY)
            .description("description")
            .source("https://www.vpro.nl/images")
            .sourceName("VPRO")
            .credits("Pietje Puk")
            .build()
        ))
        .lastPublished(LocalDateTime.of(2020, 6, 1, 20, 0, 0).atZone(Schedule.ZONE_ID).toInstant())
        .creationDate(LocalDateTime.of(2020, 6, 1, 19, 0, 0).atZone(Schedule.ZONE_ID).toInstant())
        .lastModified(LocalDateTime.of(2020, 6, 1, 19, 30, 0).atZone(Schedule.ZONE_ID).toInstant())
        .publishStart(LocalDateTime.of(2020, 6, 1, 19, 45, 0).atZone(Schedule.ZONE_ID).toInstant())
        .embeds(MediaTestDataBuilder.program().withEverything().build())
        .build();

    @Test
    public void withEverythingJson() throws Exception {
        Page rounded = Jackson2TestUtil.roundTripAndSimilar(Jackson2Mapper.getPublisherInstance(), page, getClass().getResourceAsStream("/page-with-everything.json"));
        assertThat(rounded).isEqualTo(page);
    }

    @Test
    public void withEverythingXml() throws Exception {
        String resource = "/page-with-everything.xml";
        Page rounded = JAXBTestUtil
            .roundTripAndSimilar(page, getClass().getResourceAsStream(resource))
            ;
        assertThat(rounded).isEqualTo(page);

        Xmlns.SCHEMA.newValidator().validate(new StreamSource(getClass().getResourceAsStream(resource)));

    }

}
