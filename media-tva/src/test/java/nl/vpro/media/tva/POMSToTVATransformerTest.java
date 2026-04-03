package nl.vpro.media.tva;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import jakarta.xml.bind.JAXBException;

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
        Transform.setTVAConfiguration(TVATransformerTest.SAXON_CONFIGURATION);
    }

    @Test
    public void toTVAAndBack() throws TransformerException, ParserConfigurationException, SAXException, IOException, JAXBException {
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

        var broadcast1 = MediaTestDataBuilder
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

        var broadcast2 = MediaTestDataBuilder
            .broadcast()
            .mid("mid_2")
            .crids("tva:2")
            .mainTitle("mis season2 title", MIS)
            .subTitle("mis episode2 title", MIS)
            .mainDescription("mis description2", MIS)
            .credits(Person.builder()
                .givenName("Pietje")
                .familyName("Puk")
                .role(RoleType.GUEST)
                .externalId("whatson:12345")
                .build()
            )
            .scheduleEvent(
                Channel.XXXX,
                Instant.now().plus(Duration.ofHours(2)).truncatedTo(ChronoUnit.MINUTES),
                Duration.ofHours(1)
            )
            .episodeOf(season, 1)
            .build();

        table.add(season, series, broadcast1, broadcast2); // order (sadly) matters
        table.setScheduleIfNeeded();

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);
        Transform.toTVA(table, result);

        log.info(writer);
        var roundedObject = Transform.toMediaTable(new ByteArrayInputStream(writer.toString().getBytes(UTF_8)), Map.of());

        assertThat(roundedObject.find("mid_1")).contains(broadcast1);
        assertThat(roundedObject.find("series_1")).contains(series);
        assertThat(roundedObject.find("season_1")).contains(season);
        assertThat(roundedObject).isEqualTo(table);

    }





}
