package nl.npo.wonvpp.poms;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.xml.bind.JAXB;

import org.assertj.core.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.meeuw.time.TestClock;
import org.meeuw.util.kafka.KafkaDumpReader;

import com.fasterxml.jackson.core.JacksonException;

import nl.npo.wonvpp.domain.CatalogEntry;
import nl.npo.wonvpp.domain.Utils;
import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.MediaClassificationService;
import nl.vpro.domain.media.MediaTable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.media.broadcaster.BroadcasterServiceLocator;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
class WonToPomsMapperTest {

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
        //"20260305105904-CatalogEPG-POW_06213692-npo-fvod.json",
        "20260520145415-CatalogEPG-BV_101414096-npo-svod nieuw.json"
    })

    public void map(String file) throws JacksonException {

        MediaTable table = mapper.mapToPoms(Utils.unmarshal(getClass().getResourceAsStream("/wonvpp/%s".formatted(file))));
        log.info("" + table);
        assertThat(validator.validate(table)).isEmpty();

        JAXBTestUtil.registerNamespace(
            "urn:vpro:media:2009", "https://poms-test.omroep.nl/schema/vproMedia.xsd"
        );

        JAXBTestUtil.assertThatXml(table).isSimilarTo(getClass().getResourceAsStream("/wonvpp/%s.xml".formatted(file)));
    }


    public static Stream<Record> alloutput() throws IOException {
        return Stream.empty();
        //return alloutput("output2.tsv");
        /*return Streams.concat(
//            alloutput("output.tsv"),
            //alloutput("output2.tsv"),
            //alloutput("output3.tsv")
        );*/

    }
    public static Stream<KafkaDumpReader.Record> alloutput(String output) throws IOException {
        InputStream in = WonToPomsMapperTest.class.getResourceAsStream("/wonvpp/%s".formatted(output));
        return KafkaDumpReader.readTSV(in)
            .peek(r -> log.info("{}", r));

    }

    List<CatalogEntry> entry(KafkaDumpReader.Record record) {
        byte[] bytes = record.value().getBytes(StandardCharsets.UTF_8);
        try {
            return Utils.unmarshalEnvelop(new ByteArrayInputStream(bytes), (b) -> {
                log.info("{}", new String(b, StandardCharsets.UTF_8));
            });
        } catch (Exception e) {
            log.error(":" +  new String(bytes) + " " + e.getMessage(), e);
            return null;
        }
    }

    @ParameterizedTest
    @MethodSource("alloutput")
    public void testAlls(KafkaDumpReader.Record records) {
        List<CatalogEntry> entries = entry(records);
        Assumptions.assumeThat(entries).isNotNull();
        log.info(entries);
        mapper.mapToPoms(entries)
            .forEach(table -> {
                JAXB.marshal(table, System.out);
                assertThat(validator.validate(table)).isEmpty();
            });
    }

}
