/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Test;
import org.xml.sax.SAXException;

import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Portal;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.validation.ConstraintViolations;
import nl.vpro.validation.WarningValidatorGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@Slf4j
public class ProgramUpdateTest extends MediaUpdateTest {

    @Test
    public void testIsValidWhenInvalid() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        assertThat(update.isValid()).isFalse();
    }

    @Test
    public void testErrorsWhenInvalid() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.MAIN))));
        assertThat(update.violations()).hasSize(2);
    }

    @Test
    public void testCridValidation() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.MAIN))));
        update.setAVType(AVType.AUDIO);
        update.setType(ProgramType.BROADCAST);
        update.setCrids(Collections.singletonList("crids://aa"));
        System.out.println(update.violationMessage());
        assertThat(update.violations()).hasSize(1);
    }

    @Test
    public void testIsValidForImages() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.MAIN))));
        update.setType(ProgramType.CLIP);
        update.setAVType(AVType.AUDIO);
        update.setImages(Collections.singletonList(new ImageUpdate(ImageType.BACKGROUND, "Title", "Description", new ImageLocation(null))));
        Set<? extends ConstraintViolation<MediaUpdate<Program>>> errors = update.violations();
        assertThat(errors).hasSize(1);
    }


    @Test
    public void testIsValidForLocations() throws Exception {
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
    public void testIsValidForTitles() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setType(ProgramType.CLIP);
        update.setAVType(AVType.MIXED);

        Set<? extends ConstraintViolation<MediaUpdate<Program>>> errors = update.violations();
        log.info(ConstraintViolations.humanReadable(errors));
        assertThat(errors).hasSize(1);
    }

    @Test
    public void testIsValidForTitles2() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setType(ProgramType.CLIP);
        update.setAVType(AVType.MIXED);
        update.addTitle("bla", null);

        Set<? extends ConstraintViolation<MediaUpdate<Program>>> errors = update.violations();
        log.info(ConstraintViolations.humanReadable(errors));
        assertThat(errors).hasSize(1);
    }

    @Test
    public void testFetchForOwner() throws Exception {
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
    public void testGetAVType() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setAVType(AVType.MIXED);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" avType=\"MIXED\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\"><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetEmbeddable() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setEmbeddable(false);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"false\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetPublishStart() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setPublishStartInstant(Instant.ofEpochMilli(4444));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program publishStart=\"1970-01-01T01:00:04.444+01:00\" embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetPublishStop() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setPublishStopInstant(Instant.ofEpochMilli(4444));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program publishStop=\"1970-01-01T01:00:04.444+01:00\" embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetCrids() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setCrids(Collections.singletonList("crid://bds.tv/23678459"));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><crid>crid://bds.tv/23678459</crid><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetBroadcasters() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setBroadcasters(Collections.singletonList("MAX"));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><broadcaster>MAX</broadcaster><locations/><scheduleEvents/><images/><segments/></program>";

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
    public void testGetPortalRestrictions() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setPortalRestrictions(Arrays.asList(new PortalRestrictionUpdate(new PortalRestriction(new Portal("3VOOR12_GRONINGEN", "3voor12 Groningen"))),
            new PortalRestrictionUpdate(new PortalRestriction(new Portal("STERREN24", "Sterren24"), Instant.ofEpochMilli(0), Instant.ofEpochMilli(1000000)))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><exclusive>3VOOR12_GRONINGEN</exclusive><exclusive stop=\"1970-01-01T01:16:40+01:00\" start=\"1970-01-01T01:00:00+01:00\">STERREN24</exclusive><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetGeoRestrictions() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setGeoRestrictions(new TreeSet<>(Arrays.asList(
            new GeoRestrictionUpdate(new GeoRestriction(Region.BENELUX)), new GeoRestrictionUpdate(new GeoRestriction(Region.NL, Instant.ofEpochMilli(0), Instant.ofEpochMilli((1000000)))))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <region start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:16:40+01:00\" platform=\"INTERNETVOD\">NL</region>\n" +
            "    <region platform=\"INTERNETVOD\">BENELUX</region>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetGeoRestrictionsReverse() throws Exception {
        String input = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><region>BENELUX</region><region stop=\"1970-01-01T01:16:40+01:00\" start=\"1970-01-01T01:00:00+01:00\">NL</region><locations/><scheduleEvents/><images/><segments/></program>";
        ProgramUpdate update = JAXB.unmarshal(new StringReader(input), ProgramUpdate.class);

        assertThat(update.fetch().getGeoRestrictions()).isNotEmpty();
    }

    @Test
    public void testGetTitles() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("Hoofdtitel", TextualType.MAIN))));

         String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><title type=\"MAIN\">Hoofdtitel</title><locations/><scheduleEvents/><images/><segments/></program>";
        ProgramUpdate rounded = JAXBTestUtil.roundTripAndSimilar(update, expected);
        assertThat(rounded.getTitles()).hasSize(1);
        assertThat(rounded.fetch().getTitles()).hasSize(1);
        assertThat(rounded.fetch().getTitles().first().getTitle()).isEqualTo("Hoofdtitel");


    }

    @Test
    public void testGetTitlesWitOwner() throws Exception {
        ProgramUpdate program = ProgramUpdate.create(MediaBuilder.program().titles(
            new Title("hoofdtitel omroep", OwnerType.BROADCASTER, TextualType.MAIN),
            new Title("hoofdtitel mis", OwnerType.MIS, TextualType.MAIN)).build());

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><title type=\"MAIN\">hoofdtitel omroep</title><locations/><scheduleEvents/><images/><segments/></program>";
        ProgramUpdate rounded = JAXBTestUtil.roundTripAndSimilar(program, expected);
        assertThat(rounded.getTitles()).hasSize(1);
        assertThat(rounded.fetch().getTitles()).hasSize(1);
        assertThat(rounded.fetch().getTitles().first().getTitle()).isEqualTo("hoofdtitel omroep");

    }

    @Test
    public void testGetDescriptions() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setDescriptions(new TreeSet<>(Collections.singletonList(new DescriptionUpdate("Beschrijving", TextualType.MAIN))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><description type=\"MAIN\">Beschrijving</description><locations/><scheduleEvents/><images/><segments/></program>";
        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetAVAttributes() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setAvAttributes(avAttributes());

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
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
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>\n";
        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetDuration() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setDuration(Duration.ofMillis(656565));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><duration>P0DT0H10M56.565S</duration><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetDuration2() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setDuration(Duration.ofSeconds(3 * 3600 + 46 * 60));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><duration>P0DT3H46M0.000S</duration><locations/><scheduleEvents/><images/><segments/></program>";
        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetMemberOf() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setMemberOf(new TreeSet<>(Collections.singletonList(new MemberRefUpdate(20, "urn:vpro:media:group:864"))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><memberOf highlighted=\"false\" position=\"20\">urn:vpro:media:group:864</memberOf><locations/><scheduleEvents/><images/><segments/></program>";

        ProgramUpdate rounded = JAXBTestUtil.roundTripAndSimilar(update, expected);
        assertThat(rounded.fetch().getMemberOf()).hasSize(1);

        rounded.getMemberOf().add(new MemberRefUpdate(2, "MID_123"));
        assertThat(rounded.fetch().getMemberOf()).hasSize(2);

        MemberRefUpdate first = rounded.getMemberOf().first();
        rounded.getMemberOf().remove(first);
        assertThat(rounded.fetch().getMemberOf()).hasSize(1);
        assertThat(rounded.fetch().getMemberOf().first().getMediaRef()).isEqualTo("urn:vpro:media:group:864");
        rounded.setMemberOf(new TreeSet<>(Arrays.asList(new MemberRefUpdate(3, "MID_12356"))));
        assertThat(rounded.fetch().getMemberOf().first().getMediaRef()).isEqualTo("MID_12356");


    }

    @Test
    public void testGetEmail() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setEmail(Collections.singletonList("info@vpro.nl"));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><email>info@vpro.nl</email><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetWebsites() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setWebsiteObjects(Collections.singletonList(new Website("www.vpro.nl")));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><website>www.vpro.nl</website><locations/><scheduleEvents/><images/><segments/></program>";

        ProgramUpdate found = JAXBTestUtil.roundTripAndSimilar(update, expected);
        found.getWebsites().add("http://www.npo.nl");
        assertThat(found.fetch().getWebsites()).hasSize(2);
        assertThat(found.fetch().getWebsites().get(1).getUrl()).isEqualTo("http://www.npo.nl");


    }

    @Test
    public void testGetLocations() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setLocations(new TreeSet<>(Collections.singletonList(
            new LocationUpdate("rtsp:someurl",
                Duration.ofSeconds(100),
                320, 180, 1000000, AVFileFormat.M4V))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <locations>\n" +
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
    public void testGetPerson() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setPersons(Collections.singletonList(new PersonUpdate("Pietje", "Puk", RoleType.DIRECTOR)));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><credits><person role='DIRECTOR'><givenName>Pietje</givenName><familyName>Puk</familyName></person></credits><locations /><scheduleEvents/><images/><segments/></program>";

        Program program = JAXBTestUtil.roundTripAndSimilar(update, expected).fetch();

        assertThat(program.getPersons().size()).isEqualTo(1);
        assertThat(program.getPersons().get(0).getGivenName()).isEqualTo("Pietje");
        assertThat(program.getPersons().get(0).getFamilyName()).isEqualTo("Puk");
        assertThat(program.getPersons().get(0).getRole()).isEqualTo(RoleType.DIRECTOR);
    }

    @Test
    public void testGetScheduleEvent() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setScheduleEvent(new ScheduleEventUpdate(
            Channel.RAD5,
            Instant.ofEpochMilli(97779),
            Duration.ofMillis(100))
        );

        String expected = "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
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
    public void testGetScheduleEventWithTexts() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        ScheduleEventUpdate se = new ScheduleEventUpdate(
            Channel.RAD5,
            Instant.ofEpochMilli(97779),
            Duration.ofMillis(100));
        se.addTitle(TitleUpdate.main("bla"));
        se.addDescription(new DescriptionUpdate("bloe", TextualType.LEXICO));

        update.setScheduleEvent(se);

        String expected = "<program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
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
    public void testGetRelations() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setRelations(new TreeSet<>(Collections.singletonList(new RelationUpdate(
            "ARTIST",
            "VPRO",
            "http://3voor12.vpro.nl/artists/444555",
            "Radiohead")
        )));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents/><relation uriRef=\"http://3voor12.vpro.nl/artists/444555\" broadcaster=\"VPRO\" type=\"ARTIST\">Radiohead</relation><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetImages() throws Exception {
        Image image = new Image(null, "urn:vpro.image:12345");
        image.setType(ImageType.ICON)
            .setTitle("Titel")
            .setDescription("Beschrijving")
            .setWidth(500)
            .setHeight(200).setOwner(OwnerType.BROADCASTER);

        Image image2 = new Image(null, "urn:vpro.image:12346");
        image2.setType(ImageType.ICON)
            .setTitle("Nebo Titel")
            .setDescription("Nebo Beschrijving")
            .setWidth(500)
            .setHeight(200).setOwner(OwnerType.NEBO);

        Program program = MediaBuilder.program().images(image, image2).build();
        ProgramUpdate update = ProgramUpdate.create(program);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents/><images><image highlighted=\"false\" type=\"ICON\"><title>Titel</title><description>Beschrijving</description><width>500</width><height>200</height><urn>urn:vpro.image:12345</urn></image></images><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetEpisodeOf() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setEpisodeOf(new TreeSet<>(Collections.singletonList(new MemberRefUpdate(20, "urn:vpro:media:group:864"))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents/><images/><episodeOf highlighted=\"false\" position=\"20\">urn:vpro:media:group:864</episodeOf><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testUnmarshalEpisodeOf() {
        String xml = "<program xmlns=\"urn:vpro:media:update:2009\"><episodeOf highlighted=\"false\" position=\"20\">urn:vpro:media:group:864</episodeOf></program>";
        ProgramUpdate update = JAXB.unmarshal(new StringReader(xml), ProgramUpdate.class);
        assertThat(update.getEpisodeOf().size()).isEqualTo(1);
    }

    @Test
    public void testGetSegments() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setSegments(new TreeSet<>(Collections.singletonList(SegmentUpdate.create(
            new Segment(update.fetch(), Duration.ofMillis(5555), AuthorizedDuration.ofMillis(100))))));

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents/><images/><segments><segment embeddable=\"true\"><duration>P0DT0H0M0.100S</duration><locations/><scheduleEvents/><images/><start>P0DT0H0M5.555S</start></segment></segments></program>";

        ProgramUpdate unmarshal = JAXBTestUtil.roundTripAndSimilar(update, expected);
        assertEquals(1, unmarshal.getSegments().size());
    }

    @Test
    public void testPortal() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setPortals(Collections.singletonList("STERREN24"));


        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><portal>STERREN24</portal><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testAgeRating() throws JAXBException, IOException, SAXException {
        ProgramUpdate update  = ProgramUpdate.create();
        update.setAgeRating(AgeRating._6);

        assertThat(update.getAgeRating()).isEqualTo(AgeRating._6);
        assertThat(update.fetch().getAgeRating()).isEqualTo(AgeRating._6);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><ageRating>6</ageRating><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }


    @Test
    public void testContentRating() throws JAXBException, IOException, SAXException {
        ProgramUpdate update = ProgramUpdate.create();
        update.setContentRatings(Arrays.asList(ContentRating.ANGST, ContentRating.DRUGS_EN_ALCOHOL));

        assertThat(update.getContentRatings()).containsExactly(ContentRating.ANGST, ContentRating.DRUGS_EN_ALCOHOL);
        assertThat(update.fetch().getContentRatings()).containsExactly(ContentRating.ANGST, ContentRating.DRUGS_EN_ALCOHOL);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><contentRating>ANGST</contentRating><contentRating>DRUGS_EN_ALCOHOL</contentRating><locations/><scheduleEvents/><images/><segments/></program>";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetTags() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setId(10L);
        update.setTags(new TreeSet<>(Arrays.asList("foo", "bar")));

        String expected = "<program embeddable=\"true\" urn=\"urn:vpro:media:program:10\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <tag>bar</tag>\n" +
            "    <tag>foo</tag>\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>";
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
    public void testXSD() throws Exception {
        Source xmlFile = new StreamSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program avType=\"VIDEO\" embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><broadcaster>VPRO</broadcaster><portal>STERREN24</portal><title type=\"MAIN\">bla</title><credits><person role='DIRECTOR'><givenName>Pietje</givenName><familyName>Puk</familyName></person></credits><locations/><scheduleEvents/><images/><segments/></program>"));
        SchemaFactory schemaFactory = SchemaFactory
            .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        System.out.println(getClass().getResource("/nl/vpro/domain/media/update/vproMediaUpdate.xsd"));
        Schema schema = schemaFactory.newSchema(
            getClass().getResource("/nl/vpro/domain/media/update/vproMediaUpdate.xsd")
        );
        Validator validator = schema.newValidator();
        validator.validate(xmlFile);
    }

    @Test
    public void updateRelations() {
        Program program = MediaBuilder.program().relations(Relation.ofText(RelationDefinition.of("A", "VPRO"), "aa")).build();
        ProgramUpdate update = ProgramUpdate.create(program);
        update.getRelations().first().setText("bbb");

        assertThat(update.getRelations().first().getText()).isEqualTo("bbb");


        assertThat(update.build().getRelations().first().getText()).isEqualTo("bbb");

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
                //.platform(Platform.INTERNETVOD) If you enable this, it will have the _same embargo_ ?? TODO?
                .programUrl("https://www.vpro.nl/2")
                .build();
        publishedLocation.setPublishStopInstant(Instant.now().plus(Duration.ofMinutes(10)));

        assertThat(expiredLocation.getPublishStopInstant()).isNotNull();
        assertThat(expiredLocation.getPublishStopInstant()).isBefore(Instant.now());

        assertThat(publishedLocation.getPublishStopInstant()).isNotNull();
        assertThat(publishedLocation.getPublishStopInstant()).isAfter(Instant.now());

        ProgramUpdate clip = ProgramUpdate
            .create(
                MediaBuilder.program(ProgramType.CLIP)
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

        expiredLocation = rounded.getLocations().first().toLocation();
        publishedLocation = rounded.getLocations().last().toLocation();

        assertThat(expiredLocation.getPublishStopInstant()).isNotNull(); //Used to fail
        assertThat(expiredLocation.getPublishStopInstant()).isBefore(Instant.now());

        assertThat(publishedLocation.getPublishStopInstant()).isNotNull();
        assertThat(publishedLocation.getPublishStopInstant()).isAfter(Instant.now());


    }

    @Test
    public void testMid() throws Exception {
        ProgramUpdate update = ProgramUpdate.create();
        update.setMid("bla");
        assertThat(update.mediaObject().getMid()).isNull();
        assertThat(update.getMid()).isEqualTo("bla");
        assertThat(update.build().getMid()).isEqualTo("bla");
    }
}
