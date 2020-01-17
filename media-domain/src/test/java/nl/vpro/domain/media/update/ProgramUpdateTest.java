/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.xml.bind.JAXB;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Portal;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.util.Version;
import nl.vpro.validation.ConstraintViolations;
import nl.vpro.validation.WarningValidatorGroup;

import static nl.vpro.domain.media.MediaBuilder.program;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@Slf4j
public class ProgramUpdateTest extends MediaUpdateTest {

    @Test
    public void testIsValidWhenInvalid() {
        ProgramUpdate update = ProgramUpdate.create();
        assertThat(update.isValid()).isFalse();
    }

    @Test
    public void testErrorsWhenInvalid() {
        ProgramUpdate update = ProgramUpdate.create();
        update.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.MAIN))));
        assertThat(update.violations()).hasSize(2);
    }

    @Test
    public void testCridValidation() {
        ProgramUpdate update = ProgramUpdate.create();
        update.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.MAIN))));
        update.setAVType(AVType.AUDIO);
        update.setType(ProgramType.BROADCAST);
        update.setCrids(Collections.singletonList("crids://aa"));
        log.info(update.violationMessage());
        assertThat(update.violations()).hasSize(2); // I think one for the collection, one for the element, though I would have expected 1.

    }

    @Test
    public void testIsValidForImages() {
        ProgramUpdate update = ProgramUpdate.create();
        update.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.MAIN))));
        update.setType(ProgramType.CLIP);
        update.setAVType(AVType.AUDIO);
        update.setImages(Collections.singletonList(new ImageUpdate(ImageType.BACKGROUND, "Title", "Description", new ImageLocation(null))));
        Set<? extends ConstraintViolation<MediaUpdate<Program>>> errors = update.violations();
        assertThat(errors).hasSize(1);
    }


    @Test
    public void testIsValidForLocations() {
        LocationUpdate location = LocationUpdate.builder()
            .programUrl("http:invalide.url")
            //.programUrl(null)
            .build();
        ProgramUpdate update = ProgramUpdate.create();
        update.setMainTitle("hoi");
        update.setType(ProgramType.CLIP);
        update.setAVType(AVType.MIXED);
        update.getLocations().add(location);
        Set<? extends ConstraintViolation<MediaUpdate<Program>>> errors = update.violations();
        log.info(ConstraintViolations.humanReadable(errors));

        assertThat(errors).hasSize(1);
    }

    @Test
    public void testIsValidForTitles() {
        ProgramUpdate update = ProgramUpdate.create();
        update.setType(ProgramType.CLIP);
        update.setAVType(AVType.MIXED);

        Set<? extends ConstraintViolation<MediaUpdate<Program>>> errors = update.violations();
        log.info(ConstraintViolations.humanReadable(errors));
        assertThat(errors).hasSize(1);
    }

    @Test
    public void testIsValidForTitles2() throws NoSuchFieldException, IllegalAccessException {
        ProgramUpdate update = programUpdate();
        update.setType(ProgramType.CLIP);
        update.setAVType(AVType.MIXED);
        update.addTitle("bla", TextualType.MAIN);
        TitleUpdate main = update.getTitles().first();
        Field field = TitleUpdate.class.getDeclaredField("type");
        field.setAccessible(true);
        field.set(main, null);

        Set<? extends ConstraintViolation<MediaUpdate<Program>>> errors = update.violations();
        log.info(ConstraintViolations.humanReadable(errors));
        assertThat(errors).hasSize(1);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testFetchForOwner() {
        SegmentUpdate segment = SegmentUpdate.create();
        segment.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.MAIN))));

        ProgramUpdate program = ProgramUpdate.create();
        program.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.EPISODE))));

        program.setSegments(new TreeSet<>(Collections.singletonList(segment)));

        Program result = program.fetch(OwnerType.MIS);

        assertThat(result.getTitles().first().getOwner()).isEqualTo(OwnerType.MIS);
        assertThat(result.getSegments().first().getTitles().first().getOwner()).isEqualTo(OwnerType.MIS);
    }

    @Test
    public void testFetchForMultiOwnerNullLists() {
        SegmentUpdate segment = SegmentUpdate.create();
        segment.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.MAIN))));

        ProgramUpdate programUpdate = ProgramUpdate.create();
        programUpdate.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.EPISODE))));
        programUpdate.setSegments(new TreeSet<>(Collections.singletonList(segment)));

        String expected = "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <title type=\"EPISODE\">title</title>\n" +
            "    <intentions/>\n" +
            "    <targetGroups/>\n" +
            "    <geoLocations/>\n" +
            "    <topics/>\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments>\n" +
            "        <segment embeddable=\"true\">\n" +
            "            <title type=\"MAIN\">title</title>\n" +
            "            <credits/>\n" +
            "            <locations/>\n" +
            "            <images/>\n" +
            "        </segment>\n" +
            "    </segments>\n" +
            "</program>";
        JAXBTestUtil.roundTripAndSimilar(programUpdate, expected);

        Program result = programUpdate.fetch(OwnerType.MIS);

        assertThat(result.getTitles().first().getOwner()).isEqualTo(OwnerType.MIS);
        assertThat(result.getSegments().first().getIntentions()).isEmpty();
        assertThat(result.getSegments().first().getTargetGroups()).isEmpty();

        JAXBTestUtil.roundTripAndSimilar(result, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" embeddable=\"true\" workflow=\"FOR PUBLICATION\">\n" +
            "    <title owner=\"MIS\" type=\"EPISODE\">title</title>\n" +
            "    <intentions owner=\"MIS\"/>\n" +
            "    <targetGroups owner=\"MIS\"/>\n" +
            "    <geoLocations owner=\"MIS\"/>\n" +
            "    <topics owner=\"MIS\"/>\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <images/>\n" +
            "    <scheduleEvents/>\n" +
            "    <segments>\n" +
            "        <segment type=\"SEGMENT\" embeddable=\"true\" workflow=\"FOR PUBLICATION\">\n" +
            "            <title owner=\"MIS\" type=\"MAIN\">title</title>\n" +
            "            <credits/>\n" +
            "            <descendantOf type=\"PROGRAM\"/>\n" +
            "            <locations/>\n" +
            "            <images/>\n" +
            "        </segment>\n" +
            "    </segments>\n" +
            "</program>\n");
        // imagine client does not know about these new fields targetgroups/intentions


        String withoutFields = expected.replaceAll("<intentions\\s*/>", "").replaceAll("<targetGroups\\s*/>", "");
        programUpdate = JAXB.unmarshal(new StringReader(withoutFields), ProgramUpdate.class);
        assertThat(programUpdate.intentions).isNull();
        result = programUpdate.fetch(OwnerType.MIS);
        assertFieldNull(result, "intentions");
    }

    @Test
    public void testFetchForMultiOwnerEmptyLists() {
        SegmentUpdate segment = SegmentUpdate.create();
        segment.setIntentions(new ArrayList<>());
        segment.setTargetGroups(new ArrayList<>());
        segment.setGeoLocations(new ArrayList<>());
        segment.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.MAIN))));

        ProgramUpdate program = ProgramUpdate.create();
        program.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.EPISODE))));

        program.setSegments(new TreeSet<>(Collections.singletonList(segment)));

        Program result = program.fetch(OwnerType.MIS);

        assertThat(result.getTitles().first().getOwner()).isEqualTo(OwnerType.MIS);
        assertThat(result.getSegments().first().getIntentions()).isNotEmpty();
        assertThat(result.getSegments().first().getTargetGroups()).isNotEmpty();
        assertThat(result.getSegments().first().getGeoLocations()).isNotEmpty();
    }

    @Test
    public void testGetAVType() {
        ProgramUpdate update = programUpdate();
        update.setAVType(AVType.MIXED);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<program avType=\"MIXED\" embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <geoLocations/>\n" +
                "    <topics/>\n" +
                "    <credits/>\n" +
                "    <locations/>\n" +
                "    <scheduleEvents/>\n" +
                "    <images/>\n" +
                "    <segments/>\n" +
                "</program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetEmbeddable() {
        ProgramUpdate update = programUpdate();
        update.setEmbeddable(false);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<program embeddable=\"false\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <geoLocations/>\n" +
                "    <topics/>\n" +
                "    <credits/>\n" +
                "    <locations/>\n" +
                "    <scheduleEvents/>\n" +
                "    <images/>\n" +
                "    <segments/>\n" +
                "</program>\n";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetPublishStart() {
        ProgramUpdate update = programUpdate();
        update.setPublishStartInstant(Instant.ofEpochMilli(4444));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<program publishStart=\"1970-01-01T01:00:04.444+01:00\" embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\">" +
                "<geoLocations/>" +
                "<topics/>" +
                "<credits/>" +
                "<locations/>" +
                "<scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetPublishStop() {
        ProgramUpdate update = programUpdate();
        update.setPublishStopInstant(Instant.ofEpochMilli(4444));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<program publishStop=\"1970-01-01T01:00:04.444+01:00\" embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\">" +
                "<geoLocations/><topics/><credits/><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }


    @Test
    public void testGetPublishStopFromMediaObject() {
        ProgramUpdate update = ProgramUpdate.create(
            program()
                .images(
                    Image.builder()
                        .publishStop(Instant.ofEpochMilli(5444))
                        .imageUri("urn:vpro:image:123")
                        .build()
                )
        );
        update.setVersion(null);
        update.setIntentions(null);
        update.setTargetGroups(null);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <geoLocations/>\n" +
                "    <topics/>\n" +
                "    <credits/>\n" +
                "    <locations/>\n" +
                "    <scheduleEvents/>\n" +
                "    <images>\n" +
                "        <image type=\"PICTURE\" publishStop=\"1970-01-01T01:00:05.444+01:00\" highlighted=\"false\">\n" +
                "            <urn>urn:vpro:image:123</urn>\n" +
                "        </image>\n" +
                "    </images>\n" +
                "    <segments/>\n" +
                "</program>\n";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetCrids() {
        ProgramUpdate update = programUpdate();
        update.setCrids(Collections.singletonList("crid://bds.tv/23678459"));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <crid>crid://bds.tv/23678459</crid>\n" +
                "    <geoLocations/>\n" +
                "    <topics/>\n" +
                "    <credits/>\n" +
                "    <locations/>\n" +
                "    <scheduleEvents/>\n" +
                "    <images/>\n" +
                "    <segments/>\n" +
                "</program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetBroadcasters() {
        ProgramUpdate update = programUpdate();
        update.setBroadcasters(Collections.singletonList("MAX"));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <broadcaster>MAX</broadcaster>\n" +
                "    <geoLocations/>\n" +
                "    <topics/>\n" +
                "    <credits/>\n" +
                "    <locations/>\n" +
                "    <scheduleEvents/>\n" +
                "    <images/>\n" +
                "    <segments/>\n" +
                "</program>";

        ProgramUpdate rounded = JAXBTestUtil.roundTripAndSimilar(update, expected);
        rounded.getBroadcasters().add("VPRO");
        assertThat(rounded.fetch().getBroadcasters()).hasSize(2);
        rounded.getBroadcasters().remove(0);
        assertThat(rounded.fetch().getBroadcasters()).hasSize(1);
        assertThat(rounded.fetch().getBroadcasters().get(0).getId()).isEqualTo("VPRO");
        rounded.setBroadcasters(Arrays.asList("EO"));
        assertThat(rounded.fetch().getBroadcasters().get(0).getId()).isEqualTo("EO");


    }

    @Test
    public void testGetPortalRestrictions() {
        ProgramUpdate update = programUpdate();
        update.setPortalRestrictions(Arrays.asList(new PortalRestrictionUpdate(new PortalRestriction(new Portal("3VOOR12_GRONINGEN", "3voor12 Groningen"))),
            new PortalRestrictionUpdate(new PortalRestriction(new Portal("STERREN24", "Sterren24"), Instant.ofEpochMilli(0), Instant.ofEpochMilli(1000000)))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <exclusive>3VOOR12_GRONINGEN</exclusive>\n" +
            "    <exclusive start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:16:40+01:00\">STERREN24</exclusive>\n" +
            "    <geoLocations/>\n" +
            "    <topics/>\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
                "    <scheduleEvents/>\n" +
            "    <images/>\n" +
                "    <segments/>\n" +
            "</program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetGeoRestrictions() {
        ProgramUpdate update = programUpdate();
        update.setGeoRestrictions(
            new TreeSet<>(Arrays.asList(
                new GeoRestrictionUpdate(new GeoRestriction(Region.BENELUX)), new GeoRestrictionUpdate(new GeoRestriction(Region.NL, Instant.ofEpochMilli(0), Instant.ofEpochMilli((1000000)))))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <region start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:16:40+01:00\" platform=\"INTERNETVOD\">NL</region>\n" +
                "    <region platform=\"INTERNETVOD\">BENELUX</region>\n" +
                "    <geoLocations/>\n" +
                "    <topics/>\n" +
                "    <credits/>\n" +
                "    <locations/>\n" +
                "    <scheduleEvents/>\n" +
                "    <images/>\n" +
                "    <segments/>\n" +
                "</program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetGeoRestrictionsReverse() {
        String input = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\">" +
                "<region>BENELUX</region><region stop=\"1970-01-01T01:16:40+01:00\" start=\"1970-01-01T01:00:00+01:00\">NL</region><locations/><scheduleEvents/><images/><segments/></program>";
        ProgramUpdate update = JAXB.unmarshal(new StringReader(input), ProgramUpdate.class);

        assertThat(update.fetch().getGeoRestrictions()).isNotEmpty();
    }

    @Test
    public void testGetTitles() {
        ProgramUpdate update = programUpdate();
        update.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("Hoofdtitel", TextualType.MAIN))));

         String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                 "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                 "    <title type=\"MAIN\">Hoofdtitel</title>\n" +
                 "    <geoLocations/>\n" +
                 "    <topics/>\n" +
                 "    <credits/>\n" +
                 "    <locations/>\n" +
                 "    <scheduleEvents/>\n" +
                 "    <images/>\n" +
                 "    <segments/>\n" +
                 "</program>";
        ProgramUpdate rounded = JAXBTestUtil.roundTripAndSimilar(update, expected);
        assertThat(rounded.getTitles()).hasSize(1);
        assertThat(rounded.fetch().getTitles()).hasSize(1);
        assertThat(rounded.fetch().getTitles().first().get()).isEqualTo("Hoofdtitel");


    }

    @Test
    public void testGetTitlesWitOwner() {
        ProgramUpdate program = ProgramUpdate.create(program().titles(
            new Title("hoofdtitel omroep", OwnerType.BROADCASTER, TextualType.MAIN),
            new Title("hoofdtitel mis", OwnerType.MIS, TextualType.MAIN)).build());

        program.setVersion(null);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <title type=\"MAIN\">hoofdtitel omroep</title>\n" +
            "    <intentions/>\n" +
            "    <targetGroups/>\n" +
            "    <geoLocations/>\n" +
            "    <topics/>\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>";
        ProgramUpdate rounded = JAXBTestUtil.roundTripAndSimilar(program, expected);
        assertThat(rounded.getTitles()).hasSize(1);
        assertThat(rounded.fetch().getTitles()).hasSize(1);
        assertThat(rounded.fetch().getTitles().first().get()).isEqualTo("hoofdtitel omroep");

    }

    @Test
    public void testGetDescriptions() {
        ProgramUpdate update = programUpdate();
        update.setDescriptions(new TreeSet<>(Collections.singletonList(new DescriptionUpdate("Beschrijving", TextualType.MAIN))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <description type=\"MAIN\">Beschrijving</description>\n" +
                "    <geoLocations/>\n" +
                "    <topics/>\n" +
                "    <credits/>\n" +
                "    <locations/>\n" +
                "    <scheduleEvents/>\n" +
                "    <images/>\n" +
                "    <segments/>\n" +
                "</program>";
        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetAVAttributes() {
        ProgramUpdate update = programUpdate();
        update.setAvAttributes(avAttributes());

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <geoLocations/>\n" +
            "    <topics/>\n" +
            "    <avAttributes>\n" +
            "        <bitrate>1000</bitrate>\n" +
            "        <avFileFormat>H264</avFileFormat>\n" +
            "        <videoAttributes width=\"320\" height=\"180\">\n" +
            "            <aspectRatio>16:9</aspectRatio>\n" +
            "        </videoAttributes>\n" +
            "        <audioAttributes>\n" +
            "            <channels>2</channels>\n" +
            "            <coding>AAC</coding>\n" +
            "        </audioAttributes>\n" +
            "    </avAttributes>\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>\n";
        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetDuration() {
        ProgramUpdate update = programUpdate();
        update.setDuration(Duration.ofMillis(656565));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\">" +
                "<geoLocations/><topics/><duration>P0DT0H10M56.565S</duration><credits/><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetDuration2() {
        ProgramUpdate update = programUpdate();
        update.setDuration(Duration.ofSeconds(3 * 3600 + 46 * 60));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\">" +
                "<geoLocations/><topics/><duration>P0DT3H46M0.000S</duration><credits/><locations/><scheduleEvents/><images/><segments/></program>";
        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetMemberOf() {
        ProgramUpdate update = programUpdate();
        assertThat(update.getMid()).isNull();
        update.setMemberOf(new TreeSet<>(Collections.singletonList(new MemberRefUpdate(20, "urn:vpro:media:group:864"))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\">" +
                "<geoLocations/><topics/><credits/><memberOf highlighted=\"false\" position=\"20\">urn:vpro:media:group:864</memberOf><locations/><scheduleEvents/><images/><segments/></program>";

        ProgramUpdate rounded = JAXBTestUtil.roundTripAndSimilar(update, expected);
        assertThat(rounded.fetch().getMemberOf()).hasSize(1);

        rounded.getMemberOf().add(new MemberRefUpdate(2, "MID_123"));
        assertThat(rounded.fetch().getMemberOf()).hasSize(2);

        // 2 < 20, so the first one is by mid.

        MemberRefUpdate first = rounded.getMemberOf().first();
        assertThat(first.getMediaRef()).isEqualTo("MID_123");
        rounded.getMemberOf().remove(first);
        // So, if we remove first, that the second remains.
        assertThat(rounded.fetch().getMemberOf()).hasSize(1);
        assertThat(rounded.fetch().getMemberOf().first().getMediaRef()).isEqualTo("urn:vpro:media:group:864");

        rounded.setMemberOf(new TreeSet<>(Arrays.asList(new MemberRefUpdate(3, "MID_12356"))));
        assertThat(rounded.fetch().getMemberOf().first().getMediaRef()).isEqualTo("MID_12356");


    }

    @Test
    public void testGetEmail() {
        ProgramUpdate update = programUpdate();
        update.setEmail(Collections.singletonList("info@vpro.nl"));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\">" +
                "<geoLocations/><topics/><credits/><email>info@vpro.nl</email><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetWebsites() {
        ProgramUpdate update = programUpdate();
        update.setWebsiteObjects(Collections.singletonList(new Website("www.vpro.nl")));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">" +
                "<geoLocations/><topics/><credits/><website>www.vpro.nl</website><locations/><scheduleEvents/><images/><segments/></program>";

        ProgramUpdate found = JAXBTestUtil.roundTripAndSimilar(update, expected);
        found.getWebsites().add("http://www.npo.nl");
        assertThat(found.fetch().getWebsites()).hasSize(2);
        assertThat(found.fetch().getWebsites().get(1).getUrl()).isEqualTo("http://www.npo.nl");


    }

    @Test
    public void testGetLocations() {
        ProgramUpdate update = programUpdate();
        update.setLocations(new TreeSet<>(Collections.singletonList(
            new LocationUpdate("rtsp:someurl",
                Duration.ofSeconds(100),
                320, 180, 1000000, AVFileFormat.M4V))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" + "    " +
            "<geoLocations/>\n" +
            "<topics/>\n" +
            "<credits/>\n" +
            "<locations>\n" +
            "        <location>\n" +
            "            <programUrl>rtsp:someurl</programUrl>\n" +
            "            <avAttributes>\n" +
            "                <bitrate>1000000</bitrate>\n" +
            "                <avFileFormat>M4V</avFileFormat>\n" +
            "                <videoAttributes width=\"320\" height=\"180\">\n" +
            "                    <aspectRatio>16:9</aspectRatio>\n" +
            "                </videoAttributes>\n" +
            "            </avAttributes>\n" +
            "            <duration>P0DT0H1M40.000S</duration>\n" +
            "        </location>\n" +
            "    </locations>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetPerson() {
        ProgramUpdate update = programUpdate();
        update.setCredits(Collections.singletonList(new PersonUpdate("Pietje", "Puk", RoleType.DIRECTOR)));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\">" +
                "<geoLocations/><topics/><credits><person role='DIRECTOR'><givenName>Pietje</givenName><familyName>Puk</familyName></person></credits><locations /><scheduleEvents/><images/><segments/></program>";

        Program program = JAXBTestUtil.roundTripAndSimilar(update, expected).fetch();

        assertThat(program.getCredits().size()).isEqualTo(1);
        assertThat(program.getPersons().get(0).getGivenName()).isEqualTo("Pietje");
        assertThat(program.getPersons().get(0).getFamilyName()).isEqualTo("Puk");
        assertThat(program.getCredits().get(0).getRole()).isEqualTo(RoleType.DIRECTOR);
    }

    @Test
    public void testGetScheduleEvent() {
        ProgramUpdate update = programUpdate();
        update.setScheduleEvent(new ScheduleEventUpdate(
            Channel.RAD5,
            Instant.ofEpochMilli(97779),
            Duration.ofMillis(100))
        );

        String expected = "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <geoLocations/>\n" +
            "    <topics/>\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents>\n" +
            "        <scheduleEvent channel=\"RAD5\">\n" +
            "            <start>1970-01-01T01:01:37.779+01:00</start>\n" +
            "            <duration>P0DT0H0M0.100S</duration>\n" +
            "        </scheduleEvent>\n" +
            "    </scheduleEvents>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetScheduleEventWithTexts() {
        ProgramUpdate update = programUpdate();
        ScheduleEventUpdate se = new ScheduleEventUpdate(
            Channel.RAD5,
            Instant.ofEpochMilli(97779),
            Duration.ofMillis(100));
        se.addTitle(TitleUpdate.main("bla"));
        se.addDescription(new DescriptionUpdate("bloe", TextualType.LEXICO));

        update.setScheduleEvent(se);

        String expected = "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <geoLocations/>\n" +
            "    <topics/>\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents>\n" +
            "        <scheduleEvent channel=\"RAD5\">\n" +
            "            <start>1970-01-01T01:01:37.779+01:00</start>\n" +
            "            <duration>P0DT0H0M0.100S</duration>\n" +
            "            <titles>\n" +
            "                <title type=\"MAIN\">bla</title>\n" +
            "            </titles>\n" +
            "            <descriptions>\n" +
            "                <description type=\"LEXICO\">bloe</description>\n" +
            "            </descriptions>\n" +
            "        </scheduleEvent>\n" +
            "    </scheduleEvents>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetRelations() {
        ProgramUpdate update = programUpdate();
        update.setRelations(new TreeSet<>(Collections.singletonList(new RelationUpdate(
            "ARTIST",
            "VPRO",
            "http://3voor12.vpro.nl/artists/444555",
            "Radiohead")
        )));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\">" +
                "<geoLocations/><topics/><credits/><locations/><scheduleEvents/><relation uriRef=\"http://3voor12.vpro.nl/artists/444555\" broadcaster=\"VPRO\" type=\"ARTIST\">Radiohead</relation><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetImages() {

        Image image  = Image.builder()
            .imageUri("urn:vpro.image:12345")
            .type(ImageType.ICON)
            .title("Titel")
            .description("Beschrijving")
            .width(500)
            .height(200)
            .owner(OwnerType.BROADCASTER)
            .build();


        Image image2 = Image.builder()
            .imageUri("urn:vpro.image:12346")
            .type(ImageType.ICON)
            .title("Nebo Titel")
            .description("Nebo Beschrijving")
            .width(500)
            .height(200)
            .owner(OwnerType.NEBO)
            .build();

        Program program = program().images(image, image2).build();
        ProgramUpdate update = ProgramUpdate.create(program);
        update.setVersion(null);
        update.setIntentions(null);
        update.setTargetGroups(null);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program xmlns=\"urn:vpro:media:update:2009\" embeddable=\"true\">\n" +
            "  <geoLocations/>\n" +
            "  <topics/>\n" +
            "  <credits/>\n" +
            "  <locations/>\n" +
            "  <scheduleEvents/>\n" +
            "  <images>\n" +
            "    <image highlighted=\"false\" type=\"ICON\">\n" +
            "      <title>Titel</title>\n" +
            "      <description>Beschrijving</description>\n" +
            "      <width>500</width>\n" +
            "      <height>200</height>\n" +
            "      <urn>urn:vpro.image:12345</urn>\n" +
            "    </image>\n" +
            "  </images>\n" +
            "  <segments/>\n" +
            "</program>\n";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetEpisodeOf() {
        ProgramUpdate update = programUpdate();
        update.setEpisodeOf(new TreeSet<>(Collections.singletonList(new MemberRefUpdate(20, "urn:vpro:media:group:864"))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\">" +
                "<geoLocations/><topics/><credits/><locations/><scheduleEvents/><images/><episodeOf highlighted=\"false\" position=\"20\">urn:vpro:media:group:864</episodeOf><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testUnmarshalEpisodeOf() {
        String xml = "<program xmlns=\"urn:vpro:media:update:2009\">" +
                "<episodeOf highlighted=\"false\" position=\"20\">urn:vpro:media:group:864</episodeOf></program>";
        ProgramUpdate update = JAXB.unmarshal(new StringReader(xml), ProgramUpdate.class);
        assertThat(update.getEpisodeOf().size()).isEqualTo(1);
    }

    @Test
    public void testGetSegments() {
        ProgramUpdate update = programUpdate();
        update.setVersion(Version.of(5, 12));
        update.setSegments(new TreeSet<>(Collections.singletonList(
            SegmentUpdate.create(
                new Segment(update.fetch(), Duration.ofMillis(5555), AuthorizedDuration.ofMillis(100))
            ))));
        //update.getSegments().first().setVersion(5.5f);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" version=\"5.12\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <geoLocations/>\n" +
            "    <topics/>\n" +
            "    <credits/>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments>\n" +
            "        <segment embeddable=\"true\">\n" +
            "            <intentions/>\n" +
            "            <targetGroups/>\n" +
            "            <geoLocations/>\n" +
            "            <topics/>\n" +
            "            <duration>P0DT0H0M0.100S</duration>\n" +
            "            <credits/>\n" +
            "            <locations/>\n" +
            "            <images/>\n" +
            "            <start>P0DT0H0M5.555S</start>\n" +
            "        </segment>\n" +
            "    </segments>\n" +
            "</program>";

        ProgramUpdate unmarshal = JAXBTestUtil.roundTripAndSimilar(update, expected);
        assertEquals(1, unmarshal.getSegments().size());
    }

    @Test
    public void testPortal() {
        ProgramUpdate update = programUpdate();
        update.setPortals(Collections.singletonList("STERREN24"));


        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <portal>STERREN24</portal>\n" +
                "    <geoLocations/>\n" +
                "    <topics/>\n" +
                "    <credits/>\n" +
                "    <locations/>\n" +
                "    <scheduleEvents/>\n" +
                "    <images/>\n" +
                "    <segments/>\n" +
                "</program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testAgeRating() {
        ProgramUpdate update  = ProgramUpdate.create();
        update.setVersion(Version.of(5, 5));
        update.setAgeRating(AgeRating._6);
        update.setIntentions(null);
        update.setTargetGroups(null);

        assertThat(update.getAgeRating()).isEqualTo(AgeRating._6);
        assertThat(update.fetch().getAgeRating()).isEqualTo(AgeRating._6);

        JAXBTestUtil.roundTripAndSimilar(update,
            "<program embeddable=\"true\" version=\"5.5\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                    "    <geoLocations/>\n" +
                    "    <topics/>\n" +
                    "    <credits/>\n" +
                    "    <ageRating>6</ageRating>\n" +
                    "    <locations/>\n" +
                    "    <scheduleEvents/>\n" +
                    "    <images/>\n" +
                    "    <segments/>\n" +
                    "</program>");
    }

    @Test
    public void testContentRating() {
        ProgramUpdate update = programUpdate();
        update.setContentRatings(Arrays.asList(ContentRating.ANGST, ContentRating.DRUGS_EN_ALCOHOL));

        assertThat(update.getContentRatings()).containsExactly(ContentRating.ANGST, ContentRating.DRUGS_EN_ALCOHOL);
        assertThat(update.fetch().getContentRatings()).containsExactly(ContentRating.ANGST, ContentRating.DRUGS_EN_ALCOHOL);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <geoLocations/>\n" +
                "    <topics/>\n" +
                "    <credits/>\n" +
                "    <contentRating>ANGST</contentRating>\n" +
                "    <contentRating>DRUGS_EN_ALCOHOL</contentRating>\n" +
                "    <locations/>\n" +
                "    <scheduleEvents/>\n" +
                "    <images/>\n" +
                "    <segments/>\n" +
                "</program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetTags() {
        ProgramUpdate update = programUpdate();
        update.setId(10L);
        update.setTags(new TreeSet<>(Arrays.asList("foo", "bar")));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<program embeddable=\"true\" urn=\"urn:vpro:media:program:10\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <tag>bar</tag>\n" +
                "    <tag>foo</tag>\n" +
                "    <geoLocations/>\n" +
                "    <topics/>\n" +
                "    <credits/>\n" +
                "    <locations/>\n" +
                "    <scheduleEvents/>\n" +
                "    <images/>\n" +
                "    <segments/>\n" +
                "</program>\n";
        ProgramUpdate rounded = JAXBTestUtil.roundTripAndSimilar(update, expected);
        assertThat(rounded.getTags()).hasSize(2);
        assertThat(rounded.fetch().getTags()).hasSize(2);
        rounded.setTags(new TreeSet<>(Arrays.asList("foo")));
        assertThat(rounded.fetch().getTags()).hasSize(1);

    }

    @Test
    public void testImageWithoutCredits() {
        ProgramUpdate update = ProgramUpdate.create();
        update.setAgeRating(AgeRating._6);
        update.setImages(new ImageUpdate(ImageType.LOGO, "title", null, new ImageLocation("https://placeholdit.imgix.net/~text?txt=adsfl")));
        Set<? extends ConstraintViolation<MediaUpdate<Program>>> violations = update.violations(WarningValidatorGroup.class);
        System.out.println(violations);
        assertThat(violations).isNotEmpty();
    }


    @Test
    public void testWithPredictions() {
        ProgramUpdate update = ProgramUpdate.create();
        update.setIntentions(null);
        update.setTargetGroups(null);
        update.setPredictions(new TreeSet<>(Arrays.asList(PredictionUpdate.builder().platform(Platform.INTERNETVOD).build())));

        ProgramUpdate rounded = JAXBTestUtil.roundTripAndSimilar(update, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <geoLocations/>\n" +
            "    <topics/>\n" +
            "    <credits/>\n" +
            "    <prediction>INTERNETVOD</prediction>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>");
        assertThat(rounded.getPredictions()).hasSize(1);
        assertThat(rounded.getPredictions()).hasSize(1);
    }


    @Test
    public void testWithPredictionsViaBuilder() {
        ProgramUpdate update = ProgramUpdate.create(MediaBuilder.program()
            .predictions(
                Prediction.builder().platform(Platform.INTERNETVOD).plannedAvailability(false).build(),
                Prediction.builder().platform(Platform.TVVOD).plannedAvailability(true).build()
            ));
        update.setIntentions(null);
        update.setTargetGroups(null);

        ProgramUpdate rounded = JAXBTestUtil.roundTripAndSimilar(update, "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <geoLocations/>\n" +
            "    <topics/>\n" +
            "    <credits/>\n" +
            "    <prediction>TVVOD</prediction>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>");
        assertThat(rounded.getPredictions()).hasSize(1);
    }

    @Test
    public void updateRelations() {
        Program program = program().relations(Relation.ofText(RelationDefinition.of("A", "VPRO"), "aa")).build();
        ProgramUpdate update = ProgramUpdate.create(program);
        update.getRelations().first().setText("bbb");

        assertThat(update.getRelations().first().getText()).isEqualTo("bbb");

    }

    @Test
    public void testLocations() {
        Location expiredLocation =
            Location.builder()
                .avAttributes(AVAttributes.builder().avFileFormat(AVFileFormat.H264).build())
                .platform(Platform.INTERNETVOD)
                .programUrl("https://www.vpro.nl/1")
                .build();
        expiredLocation.setPublishStopInstant(Instant.now().minus(Duration.ofMinutes(1)));

        Location publishedLocation =
            Location.builder()
                .avAttributes(AVAttributes.builder().avFileFormat(AVFileFormat.H264).build())
                //.platform(Platform.INTERNETVOD) //If you enable this, it will have the _same embargo_ ?? TODO?
                .programUrl("https://www.vpro.nl/2")
                .build();
        publishedLocation.setPublishStopInstant(Instant.now().plus(Duration.ofMinutes(10)));

        assertThat(expiredLocation.getPublishStopInstant()).isNotNull();
        assertThat(expiredLocation.getPublishStopInstant()).isBefore(Instant.now());

        assertThat(publishedLocation.getPublishStopInstant()).isNotNull();
        assertThat(publishedLocation.getPublishStopInstant()).isAfter(Instant.now());

        ProgramUpdate clip = ProgramUpdate
            .create(
                program(ProgramType.CLIP)
                    .clearBroadcasters()
                    .broadcasters("VPRO")
                    .locations(
                        expiredLocation,
                        publishedLocation
                    )
            );
        assertThat(expiredLocation.getPublishStopInstant()).isNotNull(); //Used to fail
        assertThat(expiredLocation.getPublishStopInstant()).isBefore(Instant.now());

        assertThat(publishedLocation.getPublishStopInstant()).isNotNull();
        assertThat(publishedLocation.getPublishStopInstant()).isAfter(Instant.now());

        ProgramUpdate rounded = JAXBTestUtil.roundTrip(clip);

        expiredLocation = rounded.getLocations().first().toLocation(OwnerType.BROADCASTER);
        publishedLocation = rounded.getLocations().last().toLocation(OwnerType.BROADCASTER);

        assertThat(expiredLocation.getPublishStopInstant()).isNotNull(); //Used to fail
        assertThat(expiredLocation.getPublishStopInstant()).isBefore(Instant.now());

        assertThat(publishedLocation.getPublishStopInstant()).isNotNull();
        assertThat(publishedLocation.getPublishStopInstant()).isAfter(Instant.now());


    }

    @Test
    public void testMid() {
        ProgramUpdate update = ProgramUpdate.create();
        update.setMid("bla");
        assertThat(update.getMid()).isEqualTo("bla");
    }

    @Test
    public void testCountriesAndLanguages() {
        Program program = program().countries("NL").languages("nl").build();
        ProgramUpdate update = ProgramUpdate.create(program);
        update.setIntentions(null);
        update.setTargetGroups(null);
        update.setVersion(Version.of(5, 5));
        JAXBTestUtil.roundTripAndSimilar(update, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<program embeddable=\"true\" version=\"5.5\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <country>NL</country>\n" +
                "    <language>nl</language>\n" +
                "    <geoLocations/>\n" +
                "    <topics/>\n" +
                "    <credits/>\n" +
                "    <locations/>\n" +
                "    <scheduleEvents/>\n" +
                "    <images/>\n" +
                "    <segments/>\n" +
                "</program>");

    }

    @Test


    protected ProgramUpdate programUpdate() {
        ProgramUpdate update = ProgramUpdate.create();
        update.setVersion(null);
        update.setTargetGroups(null);
        update.setIntentions(null);
        return update;
    }



    @SneakyThrows
    protected void assertFieldNull(MediaObject mo, String fieldName) {
        Field field = MediaObject.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        assertThat(field.get(mo)).isNull();
    }
}


