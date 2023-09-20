/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import java.io.*;
import java.net.http.HttpClient;
import java.time.Clock;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.validation.*;
import javax.validation.groups.Default;
import javax.xml.bind.JAXB;

import org.junit.jupiter.api.*;
import org.meeuw.util.test.BasicObjectTheory;
import org.postgresql.core.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.validation.PomsValidatorGroup;

import static java.nio.charset.StandardCharsets.UTF_8;
import static nl.vpro.domain.Changeables.CLOCK;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class LocationTest implements BasicObjectTheory<Location> {

    static final Instant NOW = Instant.parse("2021-10-26T13:00:00Z");

    @BeforeAll
    static void init() {
        log.info("Setting clock to {}", NOW);
        CLOCK.set(Clock.fixed(NOW , Schedule.ZONE_ID));
    }

    @AfterAll
    static void setClock() {
        CLOCK.remove();
    }

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void testPublishableInvalidWhenStopBeforeStart() {
        Location invalid = new Location("http://www.bla.nl", OwnerType.BROADCASTER);
        invalid
            .setPublishStartInstant(Instant.ofEpochMilli(20))
            .setPublishStopInstant(Instant.ofEpochMilli(10));

        Set<ConstraintViolation<Location>> constraintViolations = validator.validate(invalid, PomsValidatorGroup.class, Default.class);

        assertThat(constraintViolations).isNotEmpty();
    }

    @Test
    public void testInvalidProgramUrl() {
        Location invalid = new Location("sub+http//npo.npoplus.nl/video/mpeg2_hr/POW_00718800", OwnerType.BROADCASTER);
        Set<ConstraintViolation<Location>> constraintViolations = validator.validate(invalid);

        assertThat(constraintViolations).isNotEmpty();
    }

    @Test
    public void testValidProgramUrl() {
        Location invalid = new Location("sub+http://npo.npoplus.nl/video/mpeg2_hr/POW_00718800", OwnerType.BROADCASTER);
        Set<ConstraintViolation<Location>> constraintViolations = validator.validate(invalid);

        assertThat(constraintViolations).isEmpty();
    }
    @Test
    public void testAddCeresRecordWhenNew() {
        Program program = new Program(1L);
        Prediction pred = program.findOrCreatePrediction(Platform.INTERNETVOD);
        Location location = new Location();
        location.setParent(program);
        location.setPlatform(Platform.INTERNETVOD);

        assertThat(location.getAuthorityRecord()).isSameAs(pred);
    }

    @Test
    public void testEqualsNoIdDifferentURLSameOwner() {
        Location l1 = new Location("URL", OwnerType.BROADCASTER);
        Location l2 = new Location("OTHER_URL", OwnerType.BROADCASTER);

        assertThat(l1).isNotEqualTo(l2);
    }

    @Test
    public void testEqualsNoIdSameURLDifferentOwner() {
        Location l1 = new Location("URL", OwnerType.BROADCASTER);
        Location l2 = new Location("URL", OwnerType.MIS);

        assertThat(l1).isEqualTo(l2);
    }

    @Test
    public void testEqualsSameIdDifferentURLAndOwner() {
        Location l1 = new Location("URL", OwnerType.BROADCASTER);
        Location l2 = new Location("OTHER_URL", OwnerType.MIS);

        l1.setId(1L);
        l2.setId(1L);

        assertThat(l1).isEqualTo(l2);
    }

    @Test
    public void testPresentationOrder() {
        {
            Location l1 = new Location("URL", OwnerType.BROADCASTER);
            l1.setAvFileFormat(AVFileFormat.WMP);
            Location l2 = new Location("URL", OwnerType.MIS);
            l1.setAvFileFormat(AVFileFormat.MP3);

            assertThat(Location.PRESENTATION_ORDER.compare(l1, l2)).isLessThan(0);
            assertThat(Location.PRESENTATION_ORDER.compare(l2, l1)).isGreaterThan(0);
        }

        {
            Location l1 = new Location("URL", OwnerType.BROADCASTER);
            l1.setAvFileFormat(AVFileFormat.MP3);
            l1.getAvAttributes().setBitrate(2000);
            Location l2 = new Location("URL", OwnerType.MIS);
            l1.setAvFileFormat(AVFileFormat.MP3);
            l2.getAvAttributes().setBitrate(1000);

            assertThat(Location.PRESENTATION_ORDER.compare(l1, l2)).isLessThan(0);
            assertThat(Location.PRESENTATION_ORDER.compare(l2, l1)).isGreaterThan(0);
        }

        {
            Location l1 = new Location("aaaa", OwnerType.BROADCASTER);
            Location l2 = new Location("bbbb", OwnerType.MIS);

            assertThat(Location.PRESENTATION_ORDER.compare(l1, l2)).isLessThan(0);
            assertThat(Location.PRESENTATION_ORDER.compare(l2, l1)).isGreaterThan(0);

        }

    }

    @Test
    //MSE-2614
    public void testCompareTo() {
        Location location1 = new Location("http://cgi.omroep.nl/cgi-bin/streams?/vpro/19438306/surestream.rm?title=Interview Ruud Lubbers Wat trof hij aan in Darfur", OwnerType.BROADCASTER);
        Location location2 = new Location("http://cgi.omroep.nl/cgi-bin/streams?/vpro/19438306/surestream.rm?title=Interview Ruud Lubbers Wat trof hij aan in Darfur", OwnerType.BROADCASTER);
        assertThat(location1.compareTo(location2)).isEqualTo(0);

    }

    @Test
    public void fromJson() throws Exception {
        String json = """
            [ {
              "programUrl" : "http://adaptive.npostreaming.nl/u/npo/promo/1P0603VD_BEDBREAK/1P0603VD_BEDBREAK.ism",
              "avAttributes" : {
                "avFileFormat" : "HASP",
                "videoAttributes" : { }
              },
              "duration" : 30000,
              "owner" : "BROADCASTER",
              "creationDate" : 1425497262985,
              "lastModified" : 1425497263370,
              "workflow" : "PUBLISHED",
              "urn" : "urn:vpro:media:location:52197707"
            } ]""";

        Location[] locations = Jackson2Mapper.getInstance().readValue(json, Location[].class);
        Jackson2TestUtil.roundTripAndSimilar(locations, json);

        assertThat(locations[0].getDuration().toMillis()).isEqualTo(30000L);
    }


    @Test
    public void toJson() throws JsonProcessingException {
        Location loc = JAXB.unmarshal(new StringReader("""
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <location xmlns:shared="urn:vpro:shared:2009" xmlns:ns3="urn:vpro:media:2009" owner="BROADCASTER" creationDate="2015-03-06T20:22:04.051+01:00" lastModified="2015-03-06T20:22:04.457+01:00" urn="urn:vpro:media:location:52286162" workflow="PUBLISHED">
                <ns3:programUrl>http://adaptive.npostreaming.nl/u/npo/promo/2P0803MO_PODIUMWI/2P0803MO_PODIUMWI.ism</ns3:programUrl>
                <ns3:avAttributes>
                    <ns3:avFileFormat>HASP</ns3:avFileFormat>
                    <ns3:videoAttributes/>
                </ns3:avAttributes>
                <ns3:duration>PT30.000S</ns3:duration>
            </location>"""), Location.class);
        Jackson2TestUtil.assertThatJson(loc).isSimilarTo("""
            {
              "programUrl" : "http://adaptive.npostreaming.nl/u/npo/promo/2P0803MO_PODIUMWI/2P0803MO_PODIUMWI.ism",
              "avAttributes" : {
                "avFileFormat" : "HASP",
                "videoAttributes" : { }
              },
              "owner" : "BROADCASTER",
              "creationDate" : 1425669724051,
              "workflow" : "PUBLISHED",
              "duration" : 30000,
              "lastModified" : 1425669724457,
              "urn" : "urn:vpro:media:location:52286162"
            }
            """);
        //System.out.println(Jackson2Mapper.getInstance().writeValueAsString(new Location[] {loc}));
    }

    @Test
    public void sanitize() {
        assertThat(Location.sanitizedProgramUrl("http://cgi.omroep.nl/cgi-bin/streams?/tv/human/humandonderdag/bb.20040701.rm?title=Wie eegie sanie - Onze eigen dingen")).isEqualTo("http://cgi.omroep.nl/cgi-bin/streams?/tv/human/humandonderdag/bb.20040701.rm?title=Wie%20eegie%20sanie%20-%20Onze%20eigen%20dingen");
    }

    @Disabled
    @Test
    public void sanitizeAll() throws IOException {
        String dir = "/Users/michiel/npo/media/main/issues/MSE-5/MSE-5292/";
        HttpClient client = HttpClient.newBuilder().build();
        try (InputStream inputStream = new FileInputStream(new File(dir + "MSE-5292.csv"));
             OutputStream outputStream = new FileOutputStream(new File(dir + "insert_locations.sql"))) {
            final AtomicInteger count = new AtomicInteger();
            outputStream.write("insert into temp_location values ".getBytes(UTF_8));
            new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(line -> {
                String[] split = line.split("\\t");
                int number = Integer.parseInt(split[0]);
                String sanitized = Location.sanitizedProgramUrl(split[1]);

                if (! split[1].equals(sanitized)) {
                    try {
                        if (count.get() > 0) {
                            outputStream.write(",\n".getBytes(UTF_8));
                        }
                        outputStream.write("(%d, %d, '%s', '%s')"
                            .formatted( count.incrementAndGet(), number, escapeLiteral(split[1]), escapeLiteral(sanitized)).getBytes(UTF_8));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            outputStream.write("\n;".getBytes(UTF_8));

        }
    }
    @SneakyThrows
    private String escapeLiteral(String s) {
        StringBuilder escapedSanitize = new StringBuilder();
        Utils.escapeLiteral(escapedSanitize, s, true);
        return escapedSanitize.toString();
    }

    @Test
    public void builder() {
        Location location = Location.builder().platform(null).programUrl("https://bla.com/foobar.mp4").build();

        assertThat(location.getCreationInstant()).isEqualTo(NOW);
    }

    @Override
    public Arbitrary<? extends Location> datapoints() {
        Location emptyFields = new Location();
        Location withOwner = new Location(OwnerType.BROADCASTER);
        Location withUrlAndOwner = new Location("1", OwnerType.BROADCASTER);
        Location withOtherUrlAndOwner = new Location("2", OwnerType.BROADCASTER);
        return Arbitraries.of(
            emptyFields,
            withOwner,
            withUrlAndOwner,
            withOtherUrlAndOwner);
    }
}
