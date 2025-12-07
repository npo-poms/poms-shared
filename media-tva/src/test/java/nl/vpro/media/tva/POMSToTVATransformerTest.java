package nl.vpro.media.tva;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static nl.vpro.domain.media.support.OwnerType.MIS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
@Log4j2
public class POMSToTVATransformerTest {
    @BeforeAll
    public static void initAll() {
        ClassificationServiceLocator.setInstance(new MediaClassificationService());
    }

    @Test
    public void toTVAAndBack() throws TransformerException, ParserConfigurationException, SAXException, IOException {
        MediaTable table = new MediaTable();
        Group series = MediaTestDataBuilder
            .series()
            .mid("series_1")
            .mainTitle("series title", MIS)
            .build();
        Group season = MediaTestDataBuilder
            .season()
            .mid("season_1")
            .mainTitle("mis season title", MIS) // not actually used.
            .memberOf(series, 1)
            .build();

        var broadcast = MediaTestDataBuilder
            .broadcast()
            .mid("mid_1")
            .crids("tva:1")
            .mainTitle("mis season title", MIS)
            .subTitle("mis episode title", MIS)
            .mainDescription("mis description", MIS)
            .credits(Person.builder()
                .givenName("Pietje")
                .familyName("Puk")
                .role(RoleType.PRESENTER)
                .externalId("whatson:12345")
                .build()
            )
            .scheduleEvent(
                Channel.XXXX,
                Instant.now().truncatedTo(ChronoUnit.MINUTES),
                Duration.ofHours(1)
            )
            .episodeOf(season, 1)
            .build();

        table.add(season, series, broadcast); // order (sadly) matters
        table.setScheduleIfNeeded();
        StringWriter writer = new StringWriter();
        JAXB.marshal(table, writer);
        String transform = transform(new ByteArrayInputStream(writer.toString().getBytes(UTF_8)), (c) -> {
        });
        log.info(writer + "\n" + transform);
        var rounded = TVATransformerTest.transform(new ByteArrayInputStream(transform.getBytes(UTF_8)), (t)->{});

        var roundedObject = JAXB.unmarshal(new ByteArrayInputStream(rounded.getBytes(UTF_8)), MediaTable.class);

        assertThat(roundedObject.find("mid_1")).contains(broadcast);
        assertThat(roundedObject.find("series_1")).contains(series);
        assertThat(roundedObject.find("season_1")).contains(season);
        assertThat(roundedObject).isEqualTo(table);

    }


    private String transform(String resource) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        return transform(resource, (t) -> {});
    }

    private String transform(InputStream resource, Consumer<Transformer> configure) throws TransformerException, IOException, SAXException, ParserConfigurationException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Result result = new StreamResult(out);
        getTransformer(configure).transform(new StreamSource(resource), result);
        return out.toString(UTF_8);
    }

    private String transform(String resource, Consumer<Transformer> configure) throws TransformerException, IOException, SAXException, ParserConfigurationException {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        if (input == null) {
            throw new IllegalArgumentException("Could not find " + resource);
        }
        return transform(input, configure);
    }


    private Transformer getTransformer(Consumer<Transformer> configure) throws TransformerConfigurationException, ParserConfigurationException, SAXException, IOException {
        Transformer transformer = TVATransformerTest.getTransformer("/nl/vpro/media/tva/pomsToTVATransformer.xsl");
        configure.accept(transformer);
        return transformer;
    }


}
