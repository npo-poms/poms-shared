/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import nl.vpro.domain.Roles;
import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.gtaa.GTAARecord;
import nl.vpro.domain.media.gtaa.GTAAStatus;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.domain.subtitles.SubtitlesWorkflow;
import nl.vpro.domain.support.License;
import nl.vpro.domain.user.*;
import nl.vpro.i18n.Locales;

import static nl.vpro.domain.media.support.OwnerType.BROADCASTER;
import static nl.vpro.domain.media.support.OwnerType.NPO;
import static nl.vpro.domain.media.support.Workflow.MERGED;
import static nl.vpro.domain.media.support.Workflow.PUBLISHED;
import static org.meeuw.i18n.languages.ISO_639_1_Code.nl;

@SuppressWarnings({"unchecked", "deprecation", "UnusedReturnValue"})
@CanIgnoreReturnValue
public interface MediaTestDataBuilder<
        T extends MediaTestDataBuilder<T, M> &  MediaBuilder<T, M>,
        M extends MediaObject
        >
    extends MediaBuilder<T, M>, Cloneable {

    GTAARecord AMSTERDAM = GTAARecord.builder()
        .uri("http://data.beeldengeluid.nl/gtaa/31586")
        .status(GTAAStatus.approved)
        .name("Amsterdam")
        .build();
    GTAARecord UTRECHT = GTAARecord.builder()
        .uri("http://data.beeldengeluid.nl/gtaa/43996")
        .status(GTAAStatus.approved)
        .name("Utrecht (stad)")
        .build();
    GTAARecord HILVERSUM = GTAARecord.builder()
        .uri("http://data.beeldengeluid.nl/gtaa/36318")
        .status(GTAAStatus.approved)
        .name("Hilversum")
        .scopeNotes(Collections.singletonList("Nederland"))
        .build();

    AtomicLong idBase = new AtomicLong(1_000_000L);

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
     * Made object smaller and more predictable
     * (especially the creationDate we rather want a null)
     */
    default T lean() {
        return creationDate((Instant) null).workflow(null);
    }

    /**
     * Fills in the fields that are desired, and will issue warnings if not filled.
     * These may become required.
     */
    default T desiredFields() {
        return withGenres()
            .withAgeRating()
            .mainTitle("Main title")
            .withBroadcasters();

    }

    default T valid() {
        return validNew()
            .constrained()
            ;
    }

    default T validNew() {
        return constrainedNew()
            .desiredFields()
            ;

    }

    /**
     * Created an object with all required fields filled. This is used in tests which want a complete and valid object, but don't have an actual persistence layer.
     * <p>
     * Note that this also includes the {@link PublishableObject#getId()}, which normally is filled by a sequence on the database, so you should persist objects like this. Use {@link #dbConstrained()} then. Note that Hibernate >= 6.6 will not even accept entities with the id prefilled (this was sometimes done in testcases, which are changed to use {@link #dbConstrained()}
     */
    default T constrained() {
        return constrainedNew()
            .withId()
            .withMid()
            ;
    }

    /**
     * Creates an object that can be inserted into the database without further adue
     */
    default T constrainedDb() {
        T t =  constrained()
            .withFixedDates()
            .withCreatedBy()
            .withLastModifiedBy()
            ;
        t.build().locations.forEach(l ->  {
            l.setCreationInstant(LocalDate.of(2015, 3, 6).atStartOfDay(Schedule.ZONE_ID).plusHours(1).toInstant());
            l.setLastModifiedInstant(l.getCreationInstant());
            l.setCreatedBy(vproEditor());
            l.setLastModifiedBy(vproEditor());
        });
        t.build().acceptChanges(); // triggers calc32 and things like that. (seems like a hack)
        t.build().getCorrelation(); // (seems like a hack)

        return t;
    }

    /**
     * {@link #constrained()}, and also fill fields that are not actually required <em>yet</em>, but give worning here and there if not filled.
     */
    default T strictlyConstrained() {
        return constrained()
            .desiredFields()
            ;
    }

    /**
     * Fills all required fields besides 'id' (which is filled by persistence layer).
     * <p>
     * This should be used in tests which tests the persistence layer itself.
     *
     */
    default T dbConstrained() {
        return constrainedNew()
            .withMid();
    }

    default T constrainedNew() {
        return
            withAVType()
                .withBroadcasters()
                .withTitles()
                .withCreationDate()
                .withDuration();
    }

    default T withCreatedBy() {
        return createdBy(vproEditor());
    }

    default T withLastModifiedBy() {
        return lastModifiedBy(vproEditor());
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

    default T withFixedPublishStart() {
        return publishStart(Instant.EPOCH);
    }

    default T withPublishStop() {
        return publishStop(Instant.now().plus(2, ChronoUnit.HOURS));
    }
    default T withFixedPublishStop() {
        return publishStop(LocalDate.of(2500, 1, 1).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant());
    }

    default T withFixedDates() {
        return
            withFixedCreationDate()
                .withFixedLastModified()
                .withFixedLastPublished();
    }


    default T withId() {
        return id(idBase.incrementAndGet());
    }

    default T withWorkflow() {
        return published();
    }

    /**
     * Marks the object as {@link Workflow#PUBLISHED} (or {@link Workflow#MERGED} if {@code mediaObject().isMerged()})
     */
    default T published() {
        if (mediaObject().isMerged()) {
            return workflow(MERGED);
        } else {
            return workflow(PUBLISHED);
        }
    }
    default T deleted() {
        return workflow(Workflow.DELETED);
    }

    default T published(Instant lastPublished) {
        return published().lastPublished(lastPublished);
    }

    default T withUrn() {
        return id(idBase.incrementAndGet());
    }


    default T withMid() {
        if (StringUtils.isEmpty(getMid())) {
            withMid(midBase);
        }
        return (T) this;
    }

    default T withMid(AtomicLong base) {
        return mid("VPROWON_" + base.incrementAndGet());
    }


    default T withMids() {
        return withMids(midBase);
    }
    default T withFixedMids() {
        return withMids(new AtomicLong(20000L));
    }

    default T withMids(AtomicLong id) {
        if (mediaObject().getMid() == null) {
            withMid(id);
        }
        for (DescendantRef ref : mediaObject().getDescendantOf()) {
            ref.setMidRef("VPROWON_DG_" + id.incrementAndGet());

        }
        return (T) this;
    }

    default T title(String mainTitle) {
        return mainTitle(mainTitle);
    }

    AvailableSubtitles DUTCH_CAPTION = AvailableSubtitles.builder()
        .language(Locales.DUTCH)
        .type(SubtitlesType.CAPTION)
        .workflow(SubtitlesWorkflow.PUBLISHED)
        .build();

    default T withDutchCaptions() {
        mediaObject()
            .getAvailableSubtitles().add(DUTCH_CAPTION);
        return (T) this;
    }
    default T withSubtitles() {
        return withDutchCaptions();
    }
    default T clearSubtitles() {
        mediaObject().getAvailableSubtitles().clear();
        return (T) this;
    }


    default T withCrids() {
        return crids("crid://bds.tv/9876", "crid://tmp.fragment.mmbase.vpro.nl/1234");
    }

    Broadcaster BNN = Broadcaster.of("BNN");
    Broadcaster AVRO = Broadcaster.of("AVRO");

    default T withBroadcasters() {
        return broadcasters(BNN, AVRO);
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
            GeoRestriction.builder().region(Region.NL).build(),
            GeoRestriction.builder().region(Region.BENELUX).start(Instant.ofEpochMilli(0)).stop(Instant.ofEpochMilli(100000)).build(),
            GeoRestriction.builder().region(Region.NL).start(Instant.ofEpochMilli(0)).stop(Instant.ofEpochMilli(100000)).platform(Platform.TVVOD).build()
        );
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

    Genre NIEUWS_ACTUALITEITEN = new Genre("3.0.1.7.21");
    Genre DOCUMENTAIRE_NATUUR = new Genre("3.0.1.8.25");


    default T withGenres() {
        return genres(NIEUWS_ACTUALITEITEN, DOCUMENTAIRE_NATUUR);
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
        return languages( "fr").languages(UsedLanguage.dubbed(nl));
    }


    default T withAvAttributes() {
        return avAttributes(
            AVAttributes.builder()
                .bitrate(1000000)
                .byteSize(2000000L)
                .avFileFormat(AVFileFormat.M4V)
                .videoAttributes(
                    VideoAttributes.builder()
                        .videoCoding("VCODEC")
                        .horizontalSize(640)
                        .verticalSize(320)
                        .color(ColorType.BLACK_AND_WHITE)
                        .fps(50f)
                        .build()
                )
                .audioAttributes(
                    AudioAttributes.builder()
                        .audioCoding("ACODEC")
                        .language(Locales.NETHERLANDISH)
                        .numberOfChannels(2)
                        .build()
                )
            .build()
        );

    }

    default T withAVType() {
        return avType(AVType.VIDEO);
    }

    default T withAspectRatio() {
        return aspectRatio(AspectRatio._16x9);
    }

    default T withDuration()  {
        return duration(Duration.of(2, ChronoUnit.HOURS));
    }

    default T withReleaseYear() {
        return releaseYear(Short.valueOf("2004"));
            }

    default T withPersons() {
        return persons(
            Person.builder()
                .givenName("Bregtje")
                .familyName("van der Haak")
                .role(RoleType.DIRECTOR)
                .gtaaUri("http://data.beeldengeluid.nl/gtaa/1234")
                .build(),
            Person.builder()
                .givenName("Hans")
                .familyName("Goedkoop")
                .role(RoleType.PRESENTER)
                .build(),
            Person.builder()
                .givenName("Meta")
                .familyName("de Vries")
                .role(RoleType.PRESENTER)
                .build(),
            Person.builder()
                .givenName("Claire")
                .familyName("Holt")
                .role(RoleType.ACTOR)
                .build());
    }
    default T withCredits() {
        return
            withPersons()
                .credits(
                    Name.builder()
                        .uri("http://data.beeldengeluid.nl/gtaa/51771")
                        .name("Doe Maar")
                        .role(RoleType.COMPOSER)
                        .scopeNote("popgroep Nederland")
                        .build()
                )
                .persons(
                    Person.builder()
                        .name("Rutte, Mark")
                        .role(RoleType.SUBJECT)
                        .uri(URI.create("http://data.beeldengeluid.nl/gtaa/149017"))
                        // .scopeNote("minister-president VVD, fractievoorzitter VVD Tweede Kamer, staatssecretaris OCW en Sociale Zaken, voorzitter JOVD")
                        .build()

                );
    }

    default T withIntentions() {
        return intentions(
            Intentions.builder()
                .values(Arrays.asList(IntentionType.ACTIVATING, IntentionType.INFORM_INDEPTH))
                .owner(OwnerType.BROADCASTER)
                .build(),
            Intentions.builder()
                .values(Arrays.asList(IntentionType.ENTERTAINMENT_INFORMATIVE, IntentionType.INFORM))
                .owner(OwnerType.NPO)
                .build()
        );
    }

    default T withGTAARecords() {
        return   withGeoLocations()
        .withPersons();
    }

    default T withGeoLocations() {
        List<GeoLocation> geoLocations1 = Arrays.asList(
            GeoLocation.builder().name("Africa")
                .scopeNote("werelddeel")
                .uri("http://data.beeldengeluid.nl/gtaa/31299")
                .role(GeoRoleType.SUBJECT).build());

        List<GeoLocation> geoLocations2 =  Arrays.asList(
            GeoLocation.builder().role(GeoRoleType.SUBJECT)
                .name("Engeland").uri("http://data.beeldengeluid.nl/gtaa/34812")
                .scopeNote("deel Groot-Brittannië")
                .gtaaStatus(GTAAStatus.approved)
                .build(),
            GeoLocation.builder()
                .name("Groot-Brittannië")
                .uri("http://data.beeldengeluid.nl/gtaa/35768")
                .role(GeoRoleType.RECORDED_IN
                ).build()
        );
        return geoLocations(
            GeoLocations.builder()
                .values(geoLocations1)
                .owner(BROADCASTER)
                .build(),
            GeoLocations.builder()
                .values(geoLocations2)
                .owner(NPO)
                .build()
        );
    }

    default T clearIntentions() {
        mediaObject().setIntentions(null);
        return (T) this;
    }

    default T withTargetGroups(){
        return targetGroups(
                TargetGroups.builder()
                    .values(Arrays.asList(TargetGroupType.ADULTS))
                    .owner(OwnerType.BROADCASTER)
                    .build(),
                TargetGroups.builder()
                    .values(Arrays.asList(TargetGroupType.KIDS_6, TargetGroupType.KIDS_12))
                    .owner(OwnerType.NPO)
                    .build()
        );
    }

    default T clearTargetGroups() {
        mediaObject().setTargetGroups(null);
        return (T) this;
    }

    default T withAwards() {
        return awards(
            "In 2003 bekroond met een Gouden Kalf",
            "Winnaar IDFA scenarioprijs 2008.",
            "De NCRV-documentaire Onverklaarbaar? van Patrick Bisschops is genomineerd voor de prestigieuze Prix Europa 2010.",
            "De jeugdfilm BlueBird won diverse internationale prijzen, waaronder de Grote Prijs op het Montreal International Children's Film Festival en de Glazen Beer van de jongerenjury op het Filmfestival van Berlijn.");
    }

    default T withMemberOf()  {
        return withMemberOf(midBase);
    }

    default T withMemberOf(AtomicLong mids) {
        Group series = group().constrained().withMid(mids).id(100L).type(GroupType.SERIES).build();
        Group season = group().constrained().withMid(mids).id(200L).type(GroupType.SEASON).build();
        try {
            season.createMemberOf(series, 1, OwnerType.BROADCASTER);
        } catch (CircularReferenceException e) {
            throw new RuntimeException(e);
        }
        Program program = program().withMid(mids).id(300L).type(ProgramType.CLIP).memberOf(series, 10).build();
        Segment segment = segment().withMid(mids).id(301L)
                .parent(program)
                .build();


        return memberOf(season, 1)
            .memberOf(segment, 2)
            .memberOf(segment, 3);
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

    Website HTTP_JOURNAAL = new Website("http://www.omroep.nl/programma/journaal");
    Website HTTP_TEGENLICHT = new Website("http://tegenlicht.vpro.nl/afleveringen/222555");
    default T withWebsites() {
        return websites(HTTP_JOURNAAL, HTTP_TEGENLICHT);
    }

    TwitterRef HASH_VPRO = new TwitterRef("#vpro");
    TwitterRef AT_TWITTER = new TwitterRef("@twitter");
    default T withTwitterRefs() {
        return twitterRefs(HASH_VPRO, AT_TWITTER);
    }

    default T withTeletext() {
        return teletext(Short.valueOf("100"));
    }



    default T withLocations() {
        Location l1 = new Location("http://player.omroep.nl/?aflID=4393288", OwnerType.NEBO);
        l1.setCreationInstant(LocalDateTime.of(2016, 3, 4, 12, 45).atZone(Schedule.ZONE_ID).toInstant());
        l1.setBitrate(1000);
        Location l2 = new Location("http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf", OwnerType.BROADCASTER);
        l2.setCreationInstant(LocalDateTime.of(2016, 3, 4, 13, 45).atZone(Schedule.ZONE_ID).toInstant());
        l2.setDuration(Duration.of(30L, ChronoUnit.MINUTES).plus(Duration.of(33, ChronoUnit.SECONDS)));
        l2.setBitrate(2000);
        Location l3 = new Location("http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf", OwnerType.BROADCASTER);
        l3.setCreationInstant(LocalDateTime.of(2016, 3, 4, 14, 45).atZone(Schedule.ZONE_ID).toInstant());
        l3.setBitrate(3000);
        Location l4 = new Location("http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v", OwnerType.BROADCASTER);
        l4.setDuration(Duration.of(10L, ChronoUnit.MINUTES));
        l4.setOffset(Duration.of(13L, ChronoUnit.MINUTES));
        l4.setCreationInstant(LocalDateTime.of(2016, 3, 4, 15, 45).atZone(Schedule.ZONE_ID).toInstant());
        l4.setBitrate(1500);

        return locations(l1, l2, l3, l4);

    }

    default T withPublishedLocations() {
        Location l1 = new Location("http://www.vpro.nl/location/1", OwnerType.BROADCASTER);
        l1.setCreationInstant(LocalDateTime.of(2017, 2, 5, 11, 42).atZone(Schedule.ZONE_ID).toInstant());
        l1.setWorkflow(PUBLISHED);
        Location l2 = new Location("http://www.npo.nl/location/2", OwnerType.NPO);
        l2.setDuration(Duration.of(10L, ChronoUnit.MINUTES));
        l2.setOffset(Duration.of(13L, ChronoUnit.MINUTES));
        l2.setCreationInstant(LocalDateTime.of(2017, 3, 4, 15, 45).atZone(Schedule.ZONE_ID).toInstant());
        l2.setWorkflow(Workflow.FOR_PUBLICATION);

        return locations(l1, l2);

    }

    RelationDefinition VPRO_LABEL     = RelationDefinition.of("LABEL", "VPRO");
    RelationDefinition AVRO_THESAURUS = RelationDefinition.of("THESAURUS", "AVRO");
    RelationDefinition VPRO_ARTIST    = RelationDefinition.of("ARTIST", "VPRO");
    RelationDefinition EO_KOOR        = RelationDefinition.of("KOOR", "EO");

    List<RelationDefinition> RELATION_DEFINITIONS = Collections.unmodifiableList(Arrays.asList(VPRO_LABEL, AVRO_THESAURUS, VPRO_ARTIST, EO_KOOR));


    default T withRelations() {
        return withRelations(true);
    }

    default T withRelations(boolean ids) {
        return withRelations(ids ? new AtomicLong(0) : null);
    }
    default T withRelations(AtomicLong ids) {
        return relations(
            new Relation(ids == null ? null : ids.incrementAndGet(), VPRO_LABEL, "http://www.bluenote.com/", "Blue Note"),
            new Relation(ids == null ? null : ids.incrementAndGet(), AVRO_THESAURUS, null, "synoniem"),
            new Relation(ids == null ? null : ids.incrementAndGet(), VPRO_ARTIST, null, "Marco Borsato"),
            new Relation(ids == null ? null : ids.incrementAndGet(), EO_KOOR, null, "Ulfts Mannenkoor"));
    }

    default T withImages() {
        return images(
            Image.builder().imageUri("urn:vpro:image:1234").title("Eerste plaatje"),
            Image.builder().imageUri("urn:vpro:image:5678").title("Tweede plaatje"),
            Image.builder().owner(OwnerType.NEBO).imageUri("urn:vpro:image:2468").title("Een plaatje met andere owner"),
            Image.builder().owner(OwnerType.NEBO).imageUri("urn:vpro:image:8888").title("Nog een plaatje met andere owner")
        );
    }

    default T withImagesWithCredits() {
        Instant fixedDate = LocalDateTime.of(2017, 5, 11, 10, 0).atZone(Schedule.ZONE_ID).toInstant();
        return images(
            Image.builder()
                .imageUri("urn:vpro:image:11234")
                .title("Eerste plaatje met credits")
                .credits("CREDITS")
                .license(License.PUBLIC_DOMAIN)
                .source("https://www.vpro.nl")
                .creationDate(fixedDate)
            ,
            Image.builder()
                .imageUri("urn:vpro:image:15678")
                .title("Tweede plaatje met credits")
                .credits("CREDITS")
                .license(License.PUBLIC_DOMAIN)
                .source("https://www.vpro.nl")
                .creationDate(fixedDate)
            ,
            // ALso some without credits
            Image.builder()
                .owner(OwnerType.NEBO)
                .imageUri("urn:vpro:image:12468")
                .title("Een plaatje met andere owner")
                .creationDate(fixedDate),
            Image.builder()
                .owner(OwnerType.NEBO)
                .imageUri("urn:vpro:image:18888")
                .title("Nog een plaatje met andere owner")
                .creationDate(fixedDate)
        );
    }

    default T withPublishedImages() {
        return images(
            image(OwnerType.BROADCASTER, "urn:vpro:image:1234", PUBLISHED),
            image(OwnerType.BROADCASTER, "urn:vpro:image:5678", PUBLISHED)
        );
    }

    default T withTopics(){
        return topics(
            Topics.builder()
                .value(
                    Topic
                        .builder()
                        .name("honden")
                        .gtaaStatus(GTAAStatus.approved)
                        .uri("http://data.beeldengeluid.nl/gtaa/25890")
                        .build())
                .value(
                    Topic
                        .builder()
                        .name("bar")
                        .gtaaStatus(GTAAStatus.candidate)
                        .uri("http://data.beeldengeluid.nl/gtaa/29064")
                        .build()
                )
                .owner(BROADCASTER)
                .build(),
            Topics.builder()
                .value(
                    Topic
                        .builder()
                        .name("hondenrennen")
                        .gtaaStatus(GTAAStatus.approved)
                        .uri("http://data.beeldengeluid.nl/gtaa/25891")
                        .build())
                .owner(OwnerType.NPO)
                .build()
        );
    }

    default T clearTopics() {
        mediaObject().getTopics().clear();
        return (T) this;
    }

    default T withAuthorityRecord() {
        return authoritativeRecord(Platform.INTERNETVOD);
    }

    @SuppressWarnings("unchecked")
    default T authoritativeRecord(Platform... platforms) {
        for (Platform platform : platforms) {
            Prediction prediction = mediaObject().findOrCreatePrediction(platform);
            prediction.setAuthority(Authority.SYSTEM);
            prediction.setPlannedAvailability(true);
        }
        return (T)this;
    }

    default T withMergedTo() {
        return mergedTo(MediaBuilder.group().type(GroupType.SEASON).build());
    }

    default T withIds() {
        return withIds(idBase);
    }
    default T withFixedIds() {
        return withIds(new AtomicLong(1));
    }

    default T withIds(@Nullable AtomicLong id) {

        if (id != null) {
            for (Image image : mediaObject().getImages()) {
                if (image.getId() == null) {
                    image.setId(id.incrementAndGet());
                }
            }
            for (Location location : mediaObject().getLocations()) {
                if (location.getId() == null) {
                    location.setId(id.incrementAndGet());
                }
            }
      /*  for (MemberRef ref : mediaObject().getMemberOf()) {

        }
        for (DescendantRef ref : mediaObject().getDescendantOf()) {

        }*/
            if (mediaObject().getId() == null) {
                id(id.incrementAndGet());
            }
        }
        return (T) this;
    }

    default T withEverything() {
        return withEverything(true);
    }

    default T withEverything(boolean ids) {
        return withEverything(ids ? new AtomicLong(1) : null, new AtomicLong(20000L));
    }


    default T withEverything(AtomicLong ids, AtomicLong mids) {
        T result =
            withMids(mids)
                .withAgeRating()
                .withAspectRatio()
                .withAuthorityRecord()
                .withAvAttributes()
                .withAVType()
                .withAwards()
                .withBroadcasters()
                .withContentRating()
                .withCountries()
                .withCreatedBy()
                .withCredits()
                .withDescendantOf()
                .withDescriptions()
                .withDuration()
                .withEmail()
                .withFixedDates()
                .withGenres()
                .withGeoRestrictions()
                .withImagesWithCredits()
                .withIntentions()
                .withTargetGroups()
                .withGeoLocations()
                .withLanguages()
                .withLastModifiedBy()
                .withLocations()
                .withMemberOf(mids)
                .withPortalRestrictions()
                .withPortals()
                .withPublishedLocations()
                .withPublishStop()
                .withFixedPublishStop()
                .withFixedPublishStart()
                .withRelations(ids != null)
                .withReleaseYear()
                .withSource()
                .withSubtitles()
                .withTags()
                .withTeletext()
                .withTitles()
                .withTopics()
                .withTwitterRefs()
                .withWebsites()
                .withWorkflow()
                .withIds(ids)
                .correctPredictions()
            ;
        return result;


    }


    static Image image(OwnerType ownerType, String urn, Workflow workflow) {
        Image image = new Image(ownerType, urn);
        PublishableObjectAccess.setWorkflow(image, workflow);
        image.setLicense(License.COPYRIGHTED);
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
        public ProgramTestDataBuilder withEverything(AtomicLong ids, AtomicLong mids) {
            AtomicLong segmentMids = mids == null ? null : new AtomicLong(30000L);
            return MediaTestDataBuilder.super
                .withEverything(ids, mids)
                .withScheduleEvents()
                .withType()
                .withEpisodeOfIfAllowed(null, null, mids)
                .withPoProgType()
                .withPredictions()
                .withSegmentsWithEveryting()
                .withFixedSegmentMids(segmentMids);
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

        public ProgramTestDataBuilder withEpisodeOf()  {
            return withEpisodeOf(null, null);
        }

        public ProgramTestDataBuilder clearEpisodeOf() {
            mediaObject.getEpisodeOf().clear();
            return this;
        }

        public ProgramTestDataBuilder withEpisodeOf(Long seriesId, Long seasonId) {
            return withEpisodeOf(seriesId, seasonId, midBase);
        }

        public ProgramTestDataBuilder withEpisodeOf(Long seriesId, Long seasonId, AtomicLong midId)  {
            Group series = MediaTestDataBuilder.group()
                .constrained()
                .type(GroupType.SERIES)
                .nullableId(seriesId)
                .withMid(midId)
                .build();

            Program program = MediaTestDataBuilder.program()
                .type(ProgramType.CLIP)
                .withMid(midId)
                .memberOf(series, 10)
                .build();

            Segment segment = MediaTestDataBuilder.segment()
                .withMid(midId)
                .parent(program)
                .build();

            Group season = MediaTestDataBuilder.group()
                .constrained()
                .type(GroupType.SEASON)
                .nullableId(seasonId)
                .withMid(midId)
                .memberOf(series, 1)
                .memberOf(segment, 2)
                .build();



            return episodeOf(season, 1);
        }

        public ProgramTestDataBuilder withScheduleEvents() {
            return scheduleEvents(
                ScheduleEvent.builder()
                    .channel(Channel.NED3)
                    .start(Instant.ofEpochMilli(100))
                    .duration(Duration.ofMillis(200))
                    .guideDay(LocalDate.of(1969, 12, 31))
                    .repeat(Repeat.original())
                    .primaryLifestyle(new Lifestyle("Praktische Familiemensen"))
                    .secondaryLifestyle(new SecondaryLifestyle("Zorgzame Duizendpoten"))
                    .mainTitle("Main ScheduleEvent Title")
                    .mainDescription("Main ScheduleEvent Description")
                    .textSubtitles("Teletekst ondertitels")
                    .textPage("888")
                    .build(),
                ScheduleEvent.builder()
                    .channel(Channel.NED3)
                    .net(new Net("ZAPP"))
                    .start(Instant.ofEpochMilli(300L + 3 * 24 * 3600 * 1000))
                    .duration(Duration.ofMillis(50))
                    .repeat(Repeat.rerun())
                    .build(),
                ScheduleEvent.builder()
                    .channel(Channel.HOLL)
                    .start(Instant.ofEpochMilli(350L + 8 * 24 * 3600 * 1000))
                    .duration(Duration.ofMillis(250))
                    .rerun(true)
                    .build(),
                ScheduleEvent.builder().channel(Channel.CONS).start(Instant.ofEpochMilli(600L + 10 * 24 * 3600 * 1000)).duration(Duration.ofMillis(200L)).rerun(true).build()
            );
        }

        public ProgramTestDataBuilder withScheduleEvent(LocalDateTime localDateTime, Function<ScheduleEvent, ScheduleEvent> merger) {
            return scheduleEvent(Channel.NED1, localDateTime, Duration.ofMinutes(30L), merger);
        }

        public ProgramTestDataBuilder withScheduleEvent(LocalDateTime localDateTime) {
            return withScheduleEvent(Channel.NED1, localDateTime);
        }

        public ProgramTestDataBuilder withScheduleEvent(Channel channel, LocalDateTime localDateTime) {
            return scheduleEvent(channel, localDateTime, Duration.ofMinutes(30L));
        }


        public ProgramTestDataBuilder withScheduleEvent(int year, int month, int day, int hour, int minutes, Function<ScheduleEvent, ScheduleEvent> merger) {
            return withScheduleEvent(LocalDateTime.of(year, month, day, hour, minutes), merger);
        }

        public ProgramTestDataBuilder withScheduleEvent(int year, int month, int day, int hour, int minutes) {
            return withScheduleEvent(LocalDateTime.of(year, month, day, hour, minutes));
        }

        public ProgramTestDataBuilder withEpisodeOfIfAllowed(Long seriesId, Long seasonId, AtomicLong midId)  {
            if (mediaObject().getType().hasEpisodeOf()) {
                withEpisodeOf(seriesId, seasonId, midId);
            }
            return this;

        }


        public ProgramTestDataBuilder withSegments() {

            new Segment(mediaObject.getMid() + "_1", mediaObject(), Duration.ZERO, AuthorizedDuration.ofMillis(100000));
            new Segment(mediaObject.getMid() + "_2", mediaObject(), Duration.ofMillis(100000), AuthorizedDuration.ofMillis(100000));
            new Segment(mediaObject.getMid() + "_3", mediaObject(), Duration.ofMillis(1000000), AuthorizedDuration.ofMillis(300000));
            return this;
        }

        public ProgramTestDataBuilder withSegmentsWithEveryting() {
            return
                segments(
                    MediaTestDataBuilder.segment()
                        .parent(mediaObject())
                        .withEverything()
                        .mid("VPROWON_12345_1")
                        .start(Duration.ZERO)
                        .duration(Duration.ofMillis(100000))
                        .build(),
                    MediaTestDataBuilder.segment()
                        .parent(
                            mediaObject())
                        .withEverything()
                        .mid("VPROWON_12345_2")
                        .start(Duration.ofMillis(100000))
                        .duration(Duration.ofMillis(100000))
                        .build())
                ;
        }

        public ProgramTestDataBuilder clearSegments() {
            mediaObject().getSegments().clear();
            return this;
        }

        @Override
        public ProgramTestDataBuilder withIds(AtomicLong id) {
            MediaTestDataBuilder.super.withIds(id);
            for (Segment segment : mediaObject.getSegments()) {
                MediaTestDataBuilder.segment(segment).withIds(id);
            }
            return this;

        }

        @Override
        public ProgramTestDataBuilder withMids(AtomicLong id) {
            MediaTestDataBuilder.super.withMids(id);
            for (Segment segment : mediaObject.getSegments()) {
                MediaTestDataBuilder.segment(segment).withMids(id);
            }
            return this;
        }

        protected ProgramTestDataBuilder withFixedSegmentMids(AtomicLong mids) {
            for (Segment segment : mediaObject.getSegments()) {
                MediaTestDataBuilder.segment(segment)
                    .withMids(mids);
            }
            return this;
        }


        public ProgramTestDataBuilder withPoProgType() {
            mediaObject().setPoProgTypeLegacy("Verkeersmagazine");
            return this;
        }

        public ProgramTestDataBuilder withPredictions() {
            Prediction internetVOD = new Prediction(
                Platform.INTERNETVOD, Prediction.State.REVOKED
            );
            return predictions(
                internetVOD,
                new Prediction(Platform.TVVOD)
            );
        }

        public ProgramTestDataBuilder predictions(Platform... platforms) {
            List<Prediction> predictions = new ArrayList<>();
            for(Platform p : platforms) {
                predictions.add(new Prediction(p));
            }
            predictions(predictions.toArray(new Prediction[0]));
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

        @Override
        public GroupTestDataBuilder withEverything(AtomicLong ids, AtomicLong mids)  {
            return MediaTestDataBuilder.super.withEverything(ids, mids)
                .withType()
                .withPoSeriesID()
                ;

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


        public SegmentTestDataBuilder withStart() {
            return start(Duration.ofMinutes(2));
        }


        @Override
        public SegmentTestDataBuilder constrainedNew() {
            return MediaTestDataBuilder.super.constrainedNew().
                withStart();
        }

        @Override
        public SegmentTestDataBuilder withEverything(AtomicLong ids, AtomicLong mids) {
            return MediaTestDataBuilder.super.withEverything(ids, mids)
                .withStart();

        }
    }

    String TEST_PRINCIPAL =  "editor@vpro.nl";

    static Editor vproEditor() {
        return Editor.builder()
            .principalId(TEST_PRINCIPAL)
            .displayName("Editor")
            .email("editor@vpro.nl")
            .broadcaster(new Broadcaster("VPRO", "VPRO"))
            .givenName("Test")
            .familyName("Editor")
            .lastLogin(Instant.now())
            .role(Roles.USER_ROLE)
            .build();
    }
}
