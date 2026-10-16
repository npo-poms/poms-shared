package nl.npo.wonvpp.poms;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.xml.bind.JAXB;

import org.apache.logging.log4j.Level;
import org.assertj.core.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.meeuw.time.TestClock;
import org.meeuw.util.kafka.KafkaDumpReader;

import com.fasterxml.jackson.core.JacksonException;
import com.google.common.collect.Streams;

import nl.npo.wonvpp.domain.CatalogEntry;
import nl.npo.wonvpp.domain.Utils;
import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.logging.log4j2.CaptureListFromLogger;
import nl.vpro.media.broadcaster.BroadcasterServiceLocator;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
final class WonToPomsMapperTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    static WonToPomsMapper  mapper;
    static {
        ClassificationServiceLocator.setInstance(MediaClassificationService.getInstance());
        mapper = new WonToPomsMapper(BroadcasterServiceLocator.getInstance(false, true), MediaClassificationService.getInstance(), OwnerType.AUTHORITY);
        mapper.clock = TestClock.twentyTwenty();
    }


    @ParameterizedTest
    @ValueSource(strings = {
        //"20260224151601-CatalogEPG-POW_04882561-npo-svod.json",
        //"20260224151602-CatalogEPG-POW_02934251-npo-svod.json",
        //"20260224151602-CatalogEPG-POW_05880359-npo-svod.json",
        "20260305105904-CatalogEPG-POW_06213692-npo-fvod.json",
        "20260520145415-CatalogEPG-BV_101414096-npo-svod nieuw.json",
        //"20261004134758-CatalogEPG-POW_05842932-npo-svod.json"

    })

    public void map(String file) throws JacksonException {

        MediaTable table = mapper.mapToPoms(Utils.unmarshal(getClass().getResourceAsStream("/wonvpp/%s".formatted(file))));
        log.info("" + table);

        JAXBTestUtil.registerNamespace(
            "urn:vpro:media:2009", "https://poms-test.omroep.nl/schema/vproMedia.xsd"
        );

        JAXBTestUtil.assertThatXml(table).isSimilarTo(getClass().getResourceAsStream("/wonvpp/%s.xml".formatted(file)));

        assertThat(validator.validate(table)).isEmpty();

    }


    public static Stream<KafkaDumpReader.Record> alloutput() throws IOException {
        return Streams.concat(
            alloutput("output.tsv"),
            alloutput("output2.tsv")
            //alloutput("output2.tsv"),
            //alloutput("output3.tsv")
        );

    }
    public static Stream<KafkaDumpReader.Record> alloutput(String output) throws IOException {
        InputStream in = WonToPomsMapperTest.class.getResourceAsStream("/wonvpp/%s".formatted(output));
        return KafkaDumpReader.readTSV(in)
            .peek(r -> log.debug("{}", r));

    }

    @SneakyThrows
    List<CatalogEntry> entry(KafkaDumpReader.Record record) {
        byte[] bytes = record.value().getBytes(StandardCharsets.UTF_8);
        return Utils.unmarshalEnvelop(new ByteArrayInputStream(bytes), (b) -> {
            log.debug("{}", new String(b, StandardCharsets.UTF_8));
        });
    }

    @ParameterizedTest
    @MethodSource("alloutput")
    public void testAlls(KafkaDumpReader.Record record) {
        MediaTable total =  new MediaTable();
        List<CatalogEntry> entries = entry(record);
        Assumptions.assumeThat(entries).isNotNull();
        try (var capture = CaptureListFromLogger.builder().level(Level.WARN).build()) {
            log.info(entries);
            MediaTable t = mapper.mapToPoms(entries);
            t.forEach(table -> {
                JAXB.marshal(table, System.out);
                assertThat(validator.validate(table)).isEmpty();
            });
            assertThat(capture.getEvents()).isEmpty();
        }
    }

    @Test
    void P0MS_346() throws IOException {
        MediaTable table = alloutput().map(r -> mapper.mapToPoms(entry(r))).filter(t -> t.getGroup("BV_101415096").isPresent()).findFirst().orElseThrow();
        assertThat(table.getGroup("BV_101415095")).isPresent();
        Program program = table.getProgram("BV_101415100").orElseThrow();
        Optional<DescendantRef> seriesRef = program
            .getDescendantOf()
            .stream()
            .filter(descendantRef -> descendantRef.getType() == MediaType.SERIES)
            .findFirst();
        assertThat(program.getMainTitle()).isEqualTo("Watchparty: lachen op NPO Start");
        assertThat(seriesRef).isPresent();
    }

}
