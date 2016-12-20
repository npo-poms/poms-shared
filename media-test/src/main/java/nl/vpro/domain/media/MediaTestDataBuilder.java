/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.exceptions.ModificationException;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.Portal;
import nl.vpro.domain.user.TestEditors;

public interface MediaTestDataBuilder<
        T extends MediaTestDataBuilder<T, M> &  MediaBuilder<T, M>,
        M extends MediaObject
        >
    extends MediaBuilder<T, M>, Cloneable {

    AtomicLong idBase = new AtomicLong(0L);

    AtomicLong midBase = new AtomicLong(12345L);

    static ProgramTestDataBuilder program() {
        return new ProgramTestDataBuilder();
    }

    static ProgramTestDataBuilder program(Program program) {
        return new ProgramTestDataBuilder(program);
    }

    static ProgramTestDataBuilder program(ProgramBuilder program) {
        return new ProgramTestDataBuilder(program.mediaObject());
    }

    static ProgramTestDataBuilder broadcast() {
        return new ProgramTestDataBuilder().type(ProgramType.BROADCAST);
    }

    static ProgramTestDataBuilder clip() {
        return new ProgramTestDataBuilder().type(ProgramType.CLIP);
    }

    static ProgramTestDataBuilder promo() {
        return new ProgramTestDataBuilder().type(ProgramType.PROMO);
    }

    static GroupTestDataBuilder group() {
        return new GroupTestDataBuilder();
    }

    static GroupTestDataBuilder group(Group group) {
        return new GroupTestDataBuilder(group);
    }

    static GroupTestDataBuilder group(GroupBuilder group) {
        return new GroupTestDataBuilder(group.mediaObject());
    }

    static GroupTestDataBuilder playlist() {
        return new GroupTestDataBuilder().type(GroupType.PLAYLIST);
    }

    static GroupTestDataBuilder season() {
        return new GroupTestDataBuilder().type(GroupType.SEASON);
    }

    static GroupTestDataBuilder series() {
        return new GroupTestDataBuilder().type(GroupType.SERIES);
    }

    static SegmentTestDataBuilder segment() {
        return new SegmentTestDataBuilder();
    }


    static SegmentTestDataBuilder segment(Program parent) {
        return new SegmentTestDataBuilder().parent(parent);
    }

    static SegmentTestDataBuilder segment(Segment segment) {
        return new SegmentTestDataBuilder(segment);
    }

    static SegmentTestDataBuilder segment(SegmentBuilder segment) {
        return new SegmentTestDataBuilder(segment.mediaObject());
    }


    /**
     * @deprecated This is itself a mediabuilder nowadays.
     */
    @Deprecated
    <TT extends MediaBuilder<TT, M>> MediaBuilder<TT, M> getMediaBuilder();


    default T lean() {
        return creationDate((Instant) null).workflow(null);
    }

    default T valid() {
        return constrained();
    }

    default T validNew() throws ModificationException {
        return constrainedNew();
    }

    default T constrained() {
        return constrainedNew()
            .withId()
            .withMid();
    }

    default T constrainedNew() {
        try {
            return
                withAVType()
                .withBroadcasters()
                .withTitles()
                .withCreationDate()
                .withDuration();
        } catch (ModificationException e) {
            throw new RuntimeException(e);
        }
    }

    default T withCreatedBy() {
        return createdBy(TestEditors.vproEditor());
    }

    default T withLastModifiedBy() {
        return lastModifiedBy(TestEditors.vproEditor());
    }

    default T withCreationDate() {
        return creationDate(Instant.now());
    }

    default T withFixedCreationDate() {
        return creationDate(LocalDate.of(2015, 3, 6).atStartOfDay(Schedule.ZONE_ID).toInstant());
    }

    default T withFixedLastPublished() {
        return lastPublished(LocalDate.of(2015, 3, 6).atStartOfDay(Schedule.ZONE_ID).plusHours(2).toInstant());
    }


    default T withLastModified() {
        return lastModified(Instant.now());
    }
    default T withFixedLastModified() {
        return lastModified(LocalDate.of(2015, 3, 6).atStartOfDay(Schedule.ZONE_ID).plusHours(1).toInstant());
    }

    default T withPublishStart() {
        return publishStart(Instant.now());
    }

    default T withPublishStop() {
        return publishStop(Instant.now().plus(2, ChronoUnit.HOURS));
    }

    default T withFixedDates() {
        return withFixedCreationDate().withFixedLastModified().withFixedLastPublished();
    }


    default T withId() {
        return id(idBase.incrementAndGet());
    }

    default T withWorkflow() {
        return published();
    }

    default T published() {
        if (mediaObject().isMerged()) {
            return workflow(Workflow.MERGED);
        } else {
            return workflow(Workflow.PUBLISHED);
        }
    }

    default T withUrn() {
        return id(idBase.incrementAndGet());
    }


    default T withMid() {
        return mid("VPROWON_" + midBase.incrementAndGet());
    }

    default T title(String mainTitle) {
        return mainTitle(mainTitle);
    }


    default T withSubtitles() {
        return hasSubtitles(true);
    }

    default T withCrids() {
        return crids("crid://bds.tv/9876", "crid://tmp.fragment.mmbase.vpro.nl/1234");
    }
    default T withBroadcasters() {
        return broadcasters(new Broadcaster("BNN", "BNN"), new Broadcaster("AVRO", "AVRO"));
    }

    default T withoutBroadcasters() {
        return clearBroadcasters();
    }
    default T withoutPortals() {
        return clearPortals();
    }

    default T withPortals() {
        return portals(new Portal("3VOOR12_GRONINGEN", "3voor12 Groningen"), new Portal("STERREN24", "Sterren24"));
    }

    default T withoutOrganizations() {
        return withoutBroadcasters().withoutPortals();
    }

    default T withPortalRestrictions() {
        return portalRestrictions(
            new PortalRestriction(new Portal("STERREN24", "Sterren24")),
            new PortalRestriction(new Portal("3VOOR12_GRONINGEN", "3voor12 Groningen"), Instant.ofEpochMilli(0), Instant.ofEpochMilli(100000)));
    }

    default T withGeoRestrictions() {
        return geoRestrictions(
            new GeoRestriction(Region.NL),
            new GeoRestriction(Region.BENELUX, Instant.ofEpochMilli(0), Instant.ofEpochMilli(100000)));
    }

    default T withTitles() {
        return titles(
            new Title("Main title", OwnerType.BROADCASTER, TextualType.MAIN),
            new Title("Short title", OwnerType.BROADCASTER, TextualType.SHORT),
            new Title("Main title MIS", OwnerType.MIS, TextualType.MAIN),
            new Title("Episode title MIS", OwnerType.MIS, TextualType.SUB));
    }


    default T withDescriptions() {
        return descriptions(
            new Description("Main description", OwnerType.BROADCASTER, TextualType.MAIN),
            new Description("Short description", OwnerType.BROADCASTER, TextualType.SHORT),
            new Description("Main description MIS", OwnerType.MIS, TextualType.MAIN),
            new Description("Episode description MIS", OwnerType.MIS, TextualType.EPISODE));
    }
    default T withTags() {
        return tags(new Tag("tag1"), new Tag("tag2"), new Tag("tag3"));
    }

    default T withGenres() {
        return genres(new Genre("3.0.1.7.21"), new Genre("3.0.1.8.25"));
    }

    @SuppressWarnings("unchecked")
    default T withSource() {
        if (mediaObject().getSource() == null) {
            return source("Naar het gelijknamige boek van W.F. Hermans");
        }
        return (T) this;
    }

    default T withCountries() {
        return countries("GB", "US");
    }

    default T withLanguages() {
        return languages("nl", "fr");
    }


    default T withAvAttributes() {
        return avAttributes(new AVAttributes(1000000, AVFileFormat.M4V));
    }

    default T withAVType() {
        return avType(AVType.VIDEO);
    }

    default T withAspectRatio() {
        return aspectRatio(AspectRatio._16x9);
    }

    @SuppressWarnings("unchecked")
    default T withDuration() throws ModificationException {
        return duration(java.time.Duration.of(2, ChronoUnit.HOURS));
    }

    @SuppressWarnings("unchecked")
    default T withReleaseYear() {
        return releaseYear(Short.valueOf("2004"));
            }
    default T withPersons() {
        return persons(
            new Person("Bregtje", "van der Haak", RoleType.DIRECTOR),
            new Person("Hans", "Goedkoop", RoleType.PRESENTER),
            new Person("Meta", "de Vries", RoleType.PRESENTER),
            new Person("Claire", "Holt", RoleType.ACTOR));
    }

    default T withAwards() {
        return awards(
            "In 2003 bekroond met een Gouden Kalf",
            "Winnaar IDFA scenarioprijs 2008.",
            "De NCRV-documentaire Onverklaarbaar? van Patrick Bisschops is genomineerd voor de prestigieuze Prix Europa 2010.",
            "De jeugdfilm BlueBird won diverse internationale prijzen, waaronder de Grote Prijs op het Montreal International Children's Film Festival en de Glazen Beer van de jongerenjury op het Filmfestival van Berlijn.");
    }

    default T withMemberOf() throws ModificationException {
        Group series = group().constrained().id(100L).type(GroupType.SERIES).build();

        Group season = group().constrained().id(200L).type(GroupType.SEASON).build();
        try {
            season.createMemberOf(series, 1);
        } catch(CircularReferenceException e) {
            e.printStackTrace();
        }

        return memberOf(season, 1);
    }

    default T withAgeRating() {
        return ageRating(AgeRating._12);
    }

    default T withContentRating() {
        return contentRatings(ContentRating.ANGST, ContentRating.DRUGS_EN_ALCOHOL);
    }
    default T withDescendantOf() throws CircularReferenceException {
        return descendantOf(
            new DescendantRef(null, "urn:vpro:media:program:1", MediaType.BROADCAST),
            new DescendantRef(null, "urn:vpro:media:group:2", MediaType.SERIES),
            new DescendantRef("MID_123456", null, MediaType.SEASON)
        );
    }

    default T withEmail() {
        return emails("info@npo.nl", "programma@avro.nl");
    }

    default T withWebsites() {
        return websites(new Website("http://www.omroep.nl/programma/journaal"), new Website("http://tegenlicht.vpro.nl/afleveringen/222555"));
    }

    default T withTwitterRefs() {
        return twitterRefs("#vpro", "@twitter");
    }

    default T withTeletext() {
        return teletext(Short.valueOf("100"));
    }

    default T withLocations() {
        Location l1 = new Location("http://player.omroep.nl/?aflID=4393288", OwnerType.NEBO);
        l1.setCreationInstant(LocalDateTime.of(2016, 3, 4, 12, 45).atZone(Schedule.ZONE_ID).toInstant());
        Location l2 = new Location("http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf", OwnerType.BROADCASTER);
        l2.setCreationInstant(LocalDateTime.of(2016, 3, 4, 13, 45).atZone(Schedule.ZONE_ID).toInstant());
        l2.setDuration(java.time.Duration.of(30L, ChronoUnit.MINUTES).plus(java.time.Duration.of(33, ChronoUnit.SECONDS)));
        Location l3 = new Location("http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf", OwnerType.BROADCASTER);
        l3.setCreationInstant(LocalDateTime.of(2016, 3, 4, 14, 45).atZone(Schedule.ZONE_ID).toInstant());
        Location l4 = new Location("http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v", OwnerType.BROADCASTER);
        l4.setDuration(java.time.Duration.of(10L, ChronoUnit.MINUTES));
        l4.setOffset(java.time.Duration.of(13L, ChronoUnit.MINUTES));
        l4.setCreationInstant(LocalDateTime.of(2016, 3, 4, 15, 45).atZone(Schedule.ZONE_ID).toInstant());

        return locations(l1, l2, l3, l4);

    }

    default T withScheduleEvents() {
        return scheduleEvents(
            new ScheduleEvent(Channel.NED3, Instant.ofEpochMilli(100), java.time.Duration.ofMillis(200)),
            new ScheduleEvent(Channel.NED3, new Net("ZAPP"), Instant.ofEpochMilli(300 + 3 * 24 * 3600 * 1000), java.time.Duration.ofMillis(50)),
            new ScheduleEvent(Channel.HOLL, Instant.ofEpochMilli(350 + 8 * 24 * 3600 * 1000), java.time.Duration.ofMillis(250)),
            new ScheduleEvent(Channel.CONS, Instant.ofEpochMilli(600 + 10 * 24 * 3600 * 1000), java.time.Duration.ofMillis(200))
        );
    }

    default T withScheduleEvent(LocalDateTime localDateTime, Function<ScheduleEvent, ScheduleEvent> merger) {
        return scheduleEvent(Channel.NED1, localDateTime, java.time.Duration.ofMinutes(30L), merger);
    }

    default T withScheduleEvent(LocalDateTime localDateTime) {
        return scheduleEvent(Channel.NED1, localDateTime, java.time.Duration.ofMinutes(30L));
    }

    default T withScheduleEvent(int year, int month, int day, int hour, int minutes, Function<ScheduleEvent, ScheduleEvent> merger) {
        return withScheduleEvent(LocalDateTime.of(year, month, day, hour, minutes), merger);
    }

    default T withScheduleEvent(int year, int month, int day, int hour, int minutes) {
        return withScheduleEvent(LocalDateTime.of(year, month, day, hour, minutes));
    }

    default T withRelations() {
        return relations(
            new Relation(RelationDefinition.of("LABEL", "VPRO"), "http://www.bluenote.com/", "Blue Note"),
            new Relation(RelationDefinition.of("THESAURUS", "AVRO"), null, "synoniem"),
            new Relation(RelationDefinition.of("ARTIST", "VPRO"), null, "Marco Borsato"),
            new Relation(RelationDefinition.of("KOOR", "EO"), null, "Ulfts Mannenkoor"));
    }

    default T withImages() {
        return images(
            new Image(OwnerType.BROADCASTER, "urn:vpro:image:1234"),
            new Image(OwnerType.BROADCASTER, "urn:vpro:image:5678"),
            new Image(OwnerType.NEBO, "urn:vpro:image:2468"),
            new Image(OwnerType.NEBO, "urn:vpro:image:8888")
        );
    }

    default T withImagesWithCredits() {
        return images(
                new Image(OwnerType.BROADCASTER, "urn:vpro:image:1234").setCredits("CREDITS").setLicense(License.PUBLIC_DOMAIN).setSource("SOURCE"),
                new Image(OwnerType.BROADCASTER, "urn:vpro:image:5678").setCredits("CREDITS").setLicense(License.PUBLIC_DOMAIN).setSource("SOURCE"),
                new Image(OwnerType.NEBO, "urn:vpro:image:2468"),
                new Image(OwnerType.NEBO, "urn:vpro:image:8888")
        );
    }

    default T withPublishedImages() {
        return images(
            image(OwnerType.BROADCASTER, "urn:vpro:image:1234", Workflow.PUBLISHED),
            image(OwnerType.BROADCASTER, "urn:vpro:image:5678", Workflow.PUBLISHED)
        );
    }



    default T withAuthorityRecord() {
        return authoritativeRecord(Platform.INTERNETVOD);
    }

    @SuppressWarnings("unchecked")
    default T authoritativeRecord(Platform... platforms) {
        for (Platform platform : platforms) {
            LocationAuthorityRecord.authoritative(mediaObject(), platform);
        }
        return (T)this;
    }

    default T withMergedTo() {
        return mergedTo(MediaBuilder.group().type(GroupType.SEASON).build());
    }

    static Image image(OwnerType ownerType, String urn, Workflow workflow) {
        Image image = new Image(ownerType, urn);
        image.setWorkflow(workflow);
        return image;
    }


    @Slf4j
    @ToString(callSuper = true)
    class ProgramTestDataBuilder extends MediaBuilder.AbstractProgramBuilder<ProgramTestDataBuilder> implements MediaTestDataBuilder<ProgramTestDataBuilder, Program> {

        ProgramTestDataBuilder() {
            super();
        }
        ProgramTestDataBuilder(Program program) {
            super(program);
        }
        @Override
        public MediaBuilder<MediaBuilder.ProgramBuilder, Program> getMediaBuilder() {
            ProgramBuilder builder = MediaBuilder.program(mediaObject());
            builder.mid(mid);
            return builder;
        }

        @Override
        public ProgramTestDataBuilder constrainedNew() {
            return MediaTestDataBuilder.super.constrainedNew().withType();
        }

        public ProgramTestDataBuilder withType() {
            if (mediaObject().getType() == null) {
                type(ProgramType.BROADCAST);
            }
            return this;
        }

        public ProgramTestDataBuilder withEpisodeOf() throws ModificationException {
            return withEpisodeOf(null, null);
        }

        public ProgramTestDataBuilder withEpisodeOf(Long group1, Long group2) throws ModificationException {
            Group series = MediaTestDataBuilder.group().constrained().type(GroupType.SERIES).id(group1).build();
            Group season = MediaTestDataBuilder.group().constrained().type(GroupType.SEASON).id(group2).build();
            try {
                season.createMemberOf(series, 1);
            } catch(CircularReferenceException e) {
                log.error(e.getMessage());
            }

            return episodeOf(season, 1);
        }

        public ProgramTestDataBuilder withSegments() {
            new Segment(mediaObject(), "VPROWON_12345_1", new Date(0), Duration.ofMillis(100000));
            new Segment(mediaObject(), "VPROWON_12345_2", new Date(100000), Duration.ofMillis(100000));
            new Segment(mediaObject(), "VPROWON_12345_3", new Date(1000000), Duration.ofMillis(300000));
            return this;
        }


        public ProgramTestDataBuilder withPoProgType() {
            mediaObject().setPoProgTypeLegacy("Verkeersmagazine");
            return this;
        }

        public ProgramTestDataBuilder withPredictions() {
            Prediction internetVOD = new Prediction(Platform.INTERNETVOD, Prediction.State.REVOKED);
            return predictions(internetVOD, new Prediction(Platform.TVVOD));
        }

        @SuppressWarnings("unchecked")
        public ProgramTestDataBuilder predictions(Platform... platforms) {
            List<Prediction> predictions = new ArrayList<>();
            for(Platform p : platforms) {
                predictions.add(new Prediction(p));
            }
            predictions(predictions.toArray(new Prediction[predictions.size()]));
            return this;
        }
    }

    @Slf4j
    @ToString(callSuper = true)
    class GroupTestDataBuilder extends MediaBuilder.AbstractGroupBuilder<GroupTestDataBuilder> implements MediaTestDataBuilder<GroupTestDataBuilder, Group> {

        GroupTestDataBuilder() {
            super();
        }

        GroupTestDataBuilder(Group group) {
            super(group);
        }
        @Override
        public MediaBuilder<MediaBuilder.GroupBuilder, Group> getMediaBuilder() {
            GroupBuilder builder = MediaBuilder.group(mediaObject());
            builder.mid(mid);
            return builder;
        }

        @Override
        public GroupTestDataBuilder constrainedNew() {
            return MediaTestDataBuilder.super.constrainedNew()
                .withType();
        }

        public GroupTestDataBuilder withType() {
            if (mediaObject().getType() == null) {
                return type(GroupType.PLAYLIST);
            }
            return this;
        }


        public GroupTestDataBuilder withPoSeriesID() {
            return poSeriesID("VPRO_12345");
        }

    }

    @Slf4j
    @ToString(callSuper = true)
    class SegmentTestDataBuilder extends MediaBuilder.AbstractSegmentBuilder<SegmentTestDataBuilder>
        implements MediaTestDataBuilder<SegmentTestDataBuilder, Segment> {

        SegmentTestDataBuilder() {
            super();
        }

        SegmentTestDataBuilder(Segment segment) {
            super(segment);
        }


        @Override
        public MediaBuilder<MediaBuilder.SegmentBuilder, Segment> getMediaBuilder() {
            SegmentBuilder builder = MediaBuilder.segment(mediaObject());
            builder.mid(mid);
            return builder;
        }

        public SegmentTestDataBuilder withStart() {
            return start(java.time.Duration.ofMinutes(2));
        }


        @Override
        public SegmentTestDataBuilder constrainedNew() {
            return MediaTestDataBuilder.super.constrainedNew().
                withStart();
        }
    }
}
