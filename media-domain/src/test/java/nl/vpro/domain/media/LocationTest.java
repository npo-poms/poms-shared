/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.io.StringReader;
import java.time.Instant;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import javax.xml.bind.JAXB;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.theory.ObjectTest;
import nl.vpro.validation.PomsValidatorGroup;

import static org.assertj.core.api.Assertions.assertThat;

public class LocationTest extends ObjectTest<Location> {

    @DataPoint
    public static Location nullArgument = null;

    @DataPoint
    public static Location emptyFields = new Location();

    @DataPoint
    public static Location withOwner = new Location(OwnerType.BROADCASTER);

    @DataPoint
    public static Location withUrlAndOwner = new Location("1", OwnerType.BROADCASTER);

    @DataPoint
    public static Location withOtherUrlAndOwner = new Location("2", OwnerType.BROADCASTER);

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
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
        String json = "[ {\n" +
            "  \"programUrl\" : \"http://adaptive.npostreaming.nl/u/npo/promo/1P0603VD_BEDBREAK/1P0603VD_BEDBREAK.ism\",\n" +
            "  \"avAttributes\" : {\n" +
            "    \"avFileFormat\" : \"HASP\",\n" +
            "    \"videoAttributes\" : { }\n" +
            "  },\n" +
            "  \"duration\" : 30000,\n" +
            "  \"owner\" : \"BROADCASTER\",\n" +
            "  \"creationDate\" : 1425497262985,\n" +
            "  \"lastModified\" : 1425497263370,\n" +
            "  \"workflow\" : \"PUBLISHED\",\n" +
            "  \"urn\" : \"urn:vpro:media:location:52197707\"\n" +
            "} ]";

        Location[] locations = Jackson2Mapper.getInstance().readValue(json, Location[].class);
        Jackson2TestUtil.roundTripAndSimilar(locations, json);

        assertThat(locations[0].getDuration().toMillis()).isEqualTo(30000L);
    }


    @Test
    public void toJson() throws JsonProcessingException {
        Location loc = JAXB.unmarshal(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<location xmlns:shared=\"urn:vpro:shared:2009\" xmlns:ns3=\"urn:vpro:media:2009\" owner=\"BROADCASTER\" creationDate=\"2015-03-06T20:22:04.051+01:00\" lastModified=\"2015-03-06T20:22:04.457+01:00\" urn=\"urn:vpro:media:location:52286162\" workflow=\"PUBLISHED\">\n" +
            "    <ns3:programUrl>http://adaptive.npostreaming.nl/u/npo/promo/2P0803MO_PODIUMWI/2P0803MO_PODIUMWI.ism</ns3:programUrl>\n" +
            "    <ns3:avAttributes>\n" +
            "        <ns3:avFileFormat>HASP</ns3:avFileFormat>\n" +
            "        <ns3:videoAttributes/>\n" +
            "    </ns3:avAttributes>\n" +
            "    <ns3:duration>PT30.000S</ns3:duration>\n" +
            "</location>"), Location.class);
        System.out.println(Jackson2Mapper.getInstance().writeValueAsString(new Location[] {loc}));
    }

}
