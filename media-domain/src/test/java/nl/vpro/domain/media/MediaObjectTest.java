/*
 * Copyright (C) 2008 Licensed under the Apache License, Version 2.0 VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.io.StringReader;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.validation.ConstraintViolation;
import jakarta.xml.bind.JAXB;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Isolated;
import org.meeuw.i18n.countries.Country;
import org.meeuw.i18n.regions.RegionService;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.gtaa.GTAARecord;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.i18n.Locales;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.validation.ValidationLevel;

import static nl.vpro.domain.Changeables.instant;
import static nl.vpro.domain.ValidationTestHelper.*;
import static nl.vpro.domain.media.support.OwnerType.*;
import static nl.vpro.domain.media.update.Validation.getValidator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.within;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("deprecation")
@Slf4j
@Isolated
public class MediaObjectTest {
    @BeforeAll
    public static void init() {
        Locales.setDefault(Locales.NETHERLANDISH);
        ClassificationServiceLocator.setInstance(MediaClassificationService.getInstance());

    }

    @Test
    public void testAddCrid() {
        MediaObject mediaObject = new Program();
        mediaObject.addCrid(null);
        assertThat(mediaObject.getCrids()).isEmpty();

        mediaObject.addCrid("Crid 1");
        assertThat(mediaObject.getCrids()).containsExactly("Crid 1");

        mediaObject.addCrid("Crid 2");
        assertThat(mediaObject.getCrids()).containsExactly("Crid 1", "Crid 2");

        mediaObject.addCrid("Crid 1");
        mediaObject.addCrid("Crid 2");
        assertThat(mediaObject.getCrids()).containsExactly("Crid 1", "Crid 2");
    }

    @Test
    public void testFindCredit() {

        Person person1 = Person.builder().id(1L).uri(URI.create("http://gtaa/1")).build();
        Person person2 = Person.builder().id(2L).uri(URI.create("http://gtaa/2")).build();
        Name name1 = Name.builder().id(3L).uri(URI.create("http://gtaa/3")).build();
        Name name2 = Name.builder().id(4L).uri("http://gtaa/4").build();

        MediaObject mediaObject = new Program();

        assertThat(mediaObject.findCredit(1L)).isEqualTo(null);

        mediaObject.addPerson(person1);
        mediaObject.addPerson(person2);
        mediaObject.addName(name1);
        mediaObject.addName(name2);

        assertThat(mediaObject.findCredit(1L)).isEqualTo(person1);
        assertThat(mediaObject.findCredit(4L)).isEqualTo(name2);
        assertThat(mediaObject.findCredit(5L)).isEqualTo(null);
    }

    @Test
    public void testFindPerson() {

        Person person1 = Person.builder().id(1L).uri(URI.create("http://gtaa/1")).build();
        Person person2 = Person.builder().id(2L).uri(URI.create("http://gtaa/2")).build();
        Name name1 = Name.builder().id(3L).uri("http://gtaa/3").build();
        Name name2 = Name.builder().id(4L).uri("http://gtaa/4").build();

        MediaObject mediaObject = new Program();

        assertThat(mediaObject.findPerson(1L)).isEqualTo(null);

        mediaObject.addPerson(person1);
        mediaObject.addPerson(person2);
        mediaObject.addName(name1);
        mediaObject.addName(name2);

        assertThat(mediaObject.findPerson(2L)).isEqualTo(person2);
        assertThat(mediaObject.findPerson(4L)).isEqualTo(null);
        assertThat(mediaObject.findPerson(5L)).isEqualTo(null);
    }

    @Test
    public void testFindName() {

        Person person1 = Person.builder().id(1L).uri(URI.create("http://gtaa/1")).build();
        Person person2 = Person.builder().id(2L).uri(URI.create("http://gtaa/2")).build();
        Name name1 = Name.builder().id(3L).uri("http://gtaa/3").build();
        Name name2 = Name.builder().id(4L).uri("http://gtaa/4").build();

        MediaObject mediaObject = new Program();

        assertThat(mediaObject.findName(1L)).isEqualTo(null);

        mediaObject.addPerson(person1);
        mediaObject.addPerson(person2);
        mediaObject.addName(name1);
        mediaObject.addName(name2);

        assertThat(mediaObject.findName(1L)).isEqualTo(null);
        assertThat(mediaObject.findName(4L)).isEqualTo(name2);
        assertThat(mediaObject.findName(5L)).isEqualTo(null);
    }

    @Test
    public void testFindGeoLocation(){
        GeoLocation geoLocation = GeoLocation.builder().name("Amsterdam").uri("test/123").role(GeoRoleType.RECORDED_IN).build();
        geoLocation.setId(1L);

        MediaObject mediaObject = new Program();
        final boolean result = MediaObjectOwnableLists.addValue(
                mediaObject.getGeoLocations(),
                () -> new GeoLocations(mediaObject, BROADCASTER),
                geoLocation,
                BROADCASTER
        );
        assertThat(result).isTrue();

        // rather then like so. See remark in MediaObject.
        final boolean resultDuplicate =  MediaObjectOwnableLists.addValue(
                mediaObject.getGeoLocations(),
                () -> new GeoLocations(mediaObject, BROADCASTER),
                geoLocation,
                BROADCASTER
        );
        assertThat(resultDuplicate).isFalse();

        final Optional<GeoLocation> oneResult = MediaObjectOwnableLists.find(mediaObject.geoLocations,1L, BROADCASTER);
        assertThat(oneResult.isPresent()).isTrue();

        final Optional<GeoLocation> emptyResult1 = MediaObjectOwnableLists.find(mediaObject.geoLocations,2L, BROADCASTER);
        assertThat(emptyResult1.isPresent()).isFalse();

        final Optional<GeoLocation> emptyResult2 = MediaObjectOwnableLists.find(mediaObject.geoLocations,1L, NPO);
        assertThat(emptyResult2.isPresent()).isFalse();

    }

    @Test
    public void testAddGeoLocation(){
        GeoLocation geoLocation = GeoLocation.builder().name("Amsterdam").uri("http://gtaa/123").role(GeoRoleType.RECORDED_IN).build();
        geoLocation.setId(1L);

        MediaObject mediaObject = new Program();
        final boolean result =  MediaObjectOwnableLists.addValue(
                mediaObject.getGeoLocations(),
                () -> new GeoLocations(mediaObject, BROADCASTER),
                geoLocation,
                BROADCASTER
        );
        assertThat(result).isTrue();

        //Avoid duplicates
        final GeoLocation geoLocation2 = GeoLocation.builder().name("Amsterdam").uri("http://gtaa/123").role(GeoRoleType.RECORDED_IN).build();

        final boolean resultDuplicate =  MediaObjectOwnableLists.addValue(
                mediaObject.getGeoLocations(),
                () -> new GeoLocations(mediaObject, BROADCASTER),
                geoLocation2,
                BROADCASTER
        );
        assertThat(resultDuplicate).isFalse();
        assertThat(mediaObject.getGeoLocations().size()).isEqualTo(1);
        assertThat(mediaObject.getGeoLocations().first().getValues().size()).isEqualTo(1);

        //Add second geoLocation same Owner

        geoLocation2.setGtaaRecord(GTAARecord.builder().name("AnotherAmsterdam").uri("test/124").build());
        final boolean add =  MediaObjectOwnableLists.addValue(
                mediaObject.getGeoLocations(),
                () -> new GeoLocations(mediaObject, BROADCASTER),
                geoLocation2,
                BROADCASTER
        );
        assertThat(add).isTrue();
        assertThat(mediaObject.getGeoLocations().size()).isEqualTo(1);
        assertThat(mediaObject.getGeoLocations().first().getValues().size()).isEqualTo(2);

        //Add third geoLocation different Owner
        final boolean addNewOwner =  MediaObjectOwnableLists.addValue(
                mediaObject.getGeoLocations(),
                () -> new GeoLocations(mediaObject, NPO),
                geoLocation2,
                NPO
        );
        assertThat(addNewOwner).isTrue();
        assertThat(mediaObject.getGeoLocations().size()).isEqualTo(2);
        assertThat(mediaObject.getGeoLocations().last().getValues().size()).isEqualTo(1);

    }

    @Test
    public void testRemoveGeoLocation(){
        GeoLocation geoLocation = GeoLocation.builder().name("Amsterdam").uri("test/123").role(GeoRoleType.RECORDED_IN).build();
        geoLocation.setId(1L);

        MediaObject mediaObject = new Program();
        MediaObjectOwnableLists.addValue(
                mediaObject.getGeoLocations(),
                () -> new GeoLocations(mediaObject, BROADCASTER),
                geoLocation,
                BROADCASTER
        );

        GeoLocation geoLocDiffName = GeoLocation.builder().name("Amsterdam").uri("test/1234").role(GeoRoleType.RECORDED_IN).build();
        final boolean geoLocDiffNameResult = MediaObjectOwnableLists.remove(mediaObject.geoLocations, geoLocDiffName, BROADCASTER);
        assertThat(geoLocDiffNameResult).isFalse();

        OwnerType wrongOwner = NPO;
        final boolean emptyResult2 = MediaObjectOwnableLists.remove(mediaObject.geoLocations, geoLocation, wrongOwner);
        assertThat(emptyResult2).isFalse();

        OwnerType sameOwner = BROADCASTER;
        GeoLocation sameGeoLocId = GeoLocation.builder().name("Amsterdam").uri("test/123").role(GeoRoleType.RECORDED_IN).build();
        final boolean trueResult = MediaObjectOwnableLists.remove(mediaObject.geoLocations, sameGeoLocId, sameOwner);
        assertThat(trueResult).isEqualTo(true);
    }

    @Test
    public void testFindTopic() {

        Topic topic = Topic.builder().name("kattenkwa").uri("test/123").build();
        topic.setId(1L);

        MediaObject mediaObject = new Program();
        final boolean result = MediaObjectOwnableLists.addValue(
                mediaObject.getTopics(),
                () -> new Topics(mediaObject, BROADCASTER),
                topic,
                BROADCASTER
        );
        assertThat(result).isTrue();

        final boolean resultDuplicate =  MediaObjectOwnableLists.addValue(
                mediaObject.getTopics(),
                () -> new Topics(mediaObject, BROADCASTER),
                topic,
                BROADCASTER
        );
        assertThat(resultDuplicate).isFalse();

        final Optional<Topic> oneResult = MediaObjectOwnableLists.find(mediaObject.topics,1L, BROADCASTER);
        assertThat(oneResult.isPresent()).isTrue();

        final Optional<Topic> emptyResult1 = MediaObjectOwnableLists.find(mediaObject.topics,2L, BROADCASTER);
        assertThat(emptyResult1.isPresent()).isFalse();

        final Optional<Topic> emptyResult2 = MediaObjectOwnableLists.find(mediaObject.topics,1L, NPO);
        assertThat(emptyResult2.isPresent()).isFalse();
    }

    @Test
    public void testAddTopic() {

        Topic topic1 = Topic.builder().name("kattenkwaad").uri("test/123").build();
        Topic topic2 = Topic.builder().name("kattenkwaad").uri("test/123").build();

        MediaObject mediaObject = new Program();
        final boolean result =  MediaObjectOwnableLists.addValue(
                mediaObject.getTopics(),
                () -> new Topics(mediaObject, BROADCASTER),
                topic1,
                BROADCASTER
        );
        assertThat(result).isTrue();

        //Avoid duplicates
        final boolean resultDuplicate =  MediaObjectOwnableLists.addValue(
                mediaObject.getTopics(),
                () -> new Topics(mediaObject, BROADCASTER),
                topic2,
                BROADCASTER
        );
        assertThat(resultDuplicate).isFalse();
        assertThat(mediaObject.getTopics().size()).isEqualTo(1);
        assertThat(mediaObject.getTopics().first().getValues().size()).isEqualTo(1);

        //Add second topic same Owner
        topic2.setGtaaRecord(GTAARecord.builder().name("kattenkwaad2").uri("test/124").build());
        final boolean add =  MediaObjectOwnableLists.addValue(
                mediaObject.getTopics(),
                () -> new Topics(mediaObject, BROADCASTER),
                topic2,
                BROADCASTER
        );
        assertThat(add).isTrue();
        assertThat(mediaObject.getTopics().size()).isEqualTo(1);
        assertThat(mediaObject.getTopics().first().getValues().size()).isEqualTo(2);

        //Add third topic different Owner
        final boolean addNewOwner =  MediaObjectOwnableLists.addValue(
                mediaObject.getTopics(),
                () -> new Topics(mediaObject, NPO),
                topic2,
                NPO
        );
        assertThat(addNewOwner).isTrue();
        assertThat(mediaObject.getTopics().size()).isEqualTo(2);
        assertThat(mediaObject.getTopics().last().getValues().size()).isEqualTo(1);
    }

    @Test
    public void testRemoveTopic() {

        Topic topic1 = Topic.builder().name("kattenkwaad").uri("test/123").build();
        Topic topic2 = Topic.builder().name("kattenkwaad").uri("test/1234").build();
        Topic topic3 = Topic.builder().name("kattenkwaad").uri("test/123").build();
        MediaObject mediaObject = new Program();
        MediaObjectOwnableLists.addValue(
                mediaObject.getTopics(),
                () -> new Topics(mediaObject, BROADCASTER),
                topic1,
                BROADCASTER
        );

        final boolean topicDiffNameResult = MediaObjectOwnableLists.remove(mediaObject.topics, topic2, BROADCASTER);
        assertThat(topicDiffNameResult).isFalse();

        OwnerType wrongOwner = NPO;
        final boolean emptyResult2 = MediaObjectOwnableLists.remove(mediaObject.topics, topic1, wrongOwner);
        assertThat(emptyResult2).isFalse();

        final boolean trueResult = MediaObjectOwnableLists.remove(mediaObject.topics, topic3, BROADCASTER);
        assertThat(trueResult).isEqualTo(true);
    }

    @Test
    public void testAddTitle() {
        MediaObject mediaObject = new Program();
        mediaObject.addTitle(null);
        assertThat(mediaObject.getTitles()).isEmpty();

        mediaObject.addTitle(new Title("Title 1", OwnerType.MIS, TextualType.MAIN));
        assertThat(mediaObject.getTitles().first().getParent()).isEqualTo(mediaObject);
        assertThat(mediaObject.getTitles()).hasSize(1);
        assertThat(mediaObject.getTitles().last().get()).isEqualTo("Title 1");

        mediaObject.addTitle(new Title("Title 2", OwnerType.MIS, TextualType.EPISODE));
        assertThat(mediaObject.getTitles()).hasSize(2);
        assertThat(mediaObject.getTitles().last().get()).isEqualTo("Title 2");

        mediaObject.addTitle(new Title("Title 3", OwnerType.MIS, TextualType.EPISODE));
        assertThat(mediaObject.getTitles()).hasSize(2);
        assertThat(mediaObject.getTitles().last().get()).isEqualTo("Title 3");
    }

    @Test
    public void testAddDescription() {
        MediaObject mediaObject = new Program();
        mediaObject.addDescription(null);
        assertThat(mediaObject.getDescriptions()).isEmpty();

        mediaObject.addDescription(new Description("Des 1", OwnerType.MIS, TextualType.MAIN));
        assertThat(mediaObject.getDescriptions().first().getParent()).isEqualTo(mediaObject);
        assertThat(mediaObject.getDescriptions()).hasSize(1);
        assertThat(mediaObject.getDescriptions().last().get()).isEqualTo("Des 1");

        mediaObject.addDescription(new Description("Des 2", OwnerType.MIS, TextualType.EPISODE));
        assertThat(mediaObject.getDescriptions()).hasSize(2);
        assertThat(mediaObject.getDescriptions().last().get()).isEqualTo("Des 2");

        mediaObject.addDescription(new Description("Des 3", OwnerType.MIS, TextualType.EPISODE));
        assertThat(mediaObject.getDescriptions()).hasSize(2);
        assertThat(mediaObject.getDescriptions().last().get()).isEqualTo("Des 3");
    }

    @Test
    public void testGetAncestors() throws CircularReferenceException {
        Program program = new Program();
        Group group1 = new Group(GroupType.PLAYLIST);
        Group group2 = new Group(GroupType.PLAYLIST);
        Group root = new Group(GroupType.PLAYLIST);

        program.createMemberOf(group1, 1, null);
        group1.createMemberOf(group2, 1, null);
        group2.createMemberOf(root, 1, null);

        SortedSet<MediaObject> ancestors = program.getAncestors();

        assertThat(ancestors).hasSize(3);
    }

    @Test
    public void testGetAncestorsForUniqueReferences() throws CircularReferenceException {
        Program program = new Program();
        Group group1 = new Group(GroupType.PLAYLIST);
        Group group2 = new Group(GroupType.PLAYLIST);
        Group root = new Group(GroupType.PLAYLIST);

        program.createMemberOf(group1, 1, null);
        program.createMemberOf(group2, 2, null);
        group1.createMemberOf(root, 1, null);
        group2.createMemberOf(root, 2, null);

        SortedSet<MediaObject> ancestors = program.getAncestors();

        assertThat(ancestors).hasSize(3); // somebody thought this should be 4?
    }

    @Test
    public void testGetAncestorsForUniqueReferencesWithId() throws CircularReferenceException {
        Program program = new Program();
        Group group1 = new Group(GroupType.PLAYLIST);
        group1.setId(1L);
        Group group2 = new Group(GroupType.PLAYLIST);
        group2.setId(2L);
        Group root = new Group(GroupType.PLAYLIST);
        root.setId(3L);

        program.createMemberOf(group1, 1, null);
        program.createMemberOf(group2, 2, null);
        group1.createMemberOf(root, 1, null);
        group2.createMemberOf(root, 2, null);

        SortedSet<MediaObject> ancestors = program.getAncestors();

        assertThat(ancestors).hasSize(3);
    }

    @Test
    public void testCreateMemberOfForSelf() throws CircularReferenceException {
        assertThatThrownBy(() -> {

            Group g1 = new Group();

            g1.createMemberOf(g1, 1, null);
        }).isInstanceOf(CircularReferenceException.class);
    }

    @Test
    public void testCreateMemberOfForCircularity() throws CircularReferenceException {
        assertThatThrownBy(() -> {
            Group g1 = new Group(GroupType.PLAYLIST);
            Group g2 = new Group(GroupType.PLAYLIST);
            Group g3 = new Group(GroupType.PLAYLIST);
            Group g4 = new Group(GroupType.PLAYLIST);

            g1.createMemberOf(g2, 1, null);
            g2.createMemberOf(g3, 1, null);
            g3.createMemberOf(g4, 1, null);
            g4.createMemberOf(g1, 1, null);

            //assertThat(g1.getAncestors()).hasSize(4);
        }).isInstanceOf(CircularReferenceException.class);
    }

    protected Program getTestProgram() throws CircularReferenceException {
        Program program = new Program(1L);
        program.setPredictions(Arrays.asList(new Prediction(Platform.INTERNETVOD)));
        program.setUrn("urn:vpro:media:program:123");
        program.setCreationInstant(Instant.EPOCH);
        Title t = new Title("bla", OwnerType.BROADCASTER, TextualType.MAIN);
        program.addTitle(t);
        program.addDescription("bloe", OwnerType.BROADCASTER, TextualType.MAIN);

        Group group = new Group();
        group.setUrn("urn:vpro:media:group:122");
        program.addBroadcaster(new Broadcaster("VPRO", "V.P.R.O"));
        MemberRef ref = group.createMember(program, 1, null);
        ref.setHighlighted(true);
        ref.setAdded(Instant.EPOCH);

        return program;
    }

    @Test
    public void program() throws CircularReferenceException {
        Program p1 = new Program();
        assertThat(p1.getMemberOf()).isEmpty();
        Program program = getTestProgram();
        assertThat(program.getMemberOf()).hasSize(1);
    }

    @Test
    public void testProgramValidation() {
        Program p = new Program();
        p.setType(ProgramType.BROADCAST);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        Set<ConstraintViolation<Program>> constraintViolations = getValidator().validate(p);
        assertThat(constraintViolations).hasSize(1);
    }

    @Test
    public void testMidValidation() {
        Program p = new Program();
        p.setType(ProgramType.BROADCAST);
        p.setAVType(AVType.VIDEO);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        p.setMid("foo/bar");
        Set<ConstraintViolation<Program>> constraintViolations = getValidator().validate(p);
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getMessageTemplate()).isEqualTo("{nl.vpro.constraints.mid}");
    }



    @Test
    public void testLanguageValidation() {
        Program p = new Program();
        p.setType(ProgramType.BROADCAST);
        p.setAVType(AVType.VIDEO);
        p.getGenres().add( new Genre("3.0.1.7.21"));
        p.setAgeRating(AgeRating.ALL);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        p.setLanguages(UsedLanguage.asList(new Locale("ZZ"), Locales.DUTCH));

        Set<ConstraintViolation<Program>> constraintViolations = getValidator().validate(p, ValidationLevel.WARNING.getClasses());

        assertThat(constraintViolations.iterator().next().getMessageTemplate()).startsWith("{org.meeuw.i18n.languages.validation.language.message}");
        assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("zz is een ongeldige ISO639 taalcode");
        assertThat(constraintViolations).hasSize(1);

    }

    @Test
    public void testLanguageWithCountryValidation() {
        Program p = new Program();

        p.setType(ProgramType.BROADCAST);
        p.setAVType(AVType.VIDEO);
        p.setAgeRating(AgeRating.ALL);
        p.getGenres().add(new Genre("3.0.1.7.21"));
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        p.setLanguages(UsedLanguage.asList(Locales.NETHERLANDISH, new Locale("nl", "XX")));

        List<ConstraintViolation<Program>> constraintViolations = new ArrayList<>(getValidator().validate(p, ValidationLevel.WARNING.getClasses()));
        Comparator<ConstraintViolation<Program>> comparing = Comparator.comparing(c -> c.getPropertyPath().toString());
        constraintViolations.sort(comparing.thenComparing(ConstraintViolation::getMessageTemplate));


        assertThat(constraintViolations.get(0).getMessageTemplate()).startsWith("{org.meeuw.i18n.regions.validation.region.message}");
        assertThat(constraintViolations.get(0).getMessage()).isEqualTo("nl_XX is geen geldig gebied");
        assertThat(constraintViolations).hasSize(1);
    }

    @Test
    public void validCountries() {
        List<Country> valid = new ArrayList<>();
        List<Country> invalid = new ArrayList<>();
        Stream.concat(RegionService.getInstance().values(Country.class), Stream.of((Country)null)).forEach(c -> {
            Program p = new Program();

            p.setType(ProgramType.BROADCAST);
            p.setAVType(AVType.VIDEO);
            p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
            p.addCountry(c);
            List<ConstraintViolation<Program>> constraintViolations = new ArrayList<>(getValidator().validate(p));
            if (constraintViolations.isEmpty()) {
                valid.add(c);
            } else {
                log.info(constraintViolations.toString());
                invalid.add(c);
            }
        });

        assertThat(invalid.stream().map(c -> c == null ? "null" : (c.getCode() + ":" + c.getName(Locales.NETHERLANDISH))).collect(Collectors.joining("\n"))).isEqualTo("""
              UNDEFINED:Undefined
              AC:Ascension Island
              AN:Nederlandse Antillen
              BU:Burma
              CP:Clipperton Island
              CS:Serbia and Montenegro
              DG:Diego Garcia
              EA:Ceuta, Melilla
              EU:European Union
              EZ:Eurozone
              FX:France, Metropolitan
              IC:Canary Islands
              NT:Neutral Zone
              SF:Finland
              SU:USSR
              TA:Tristan da Cunha
              TP:East Timor
              UK:United Kingdom
              XI:Noord-Ierland
              XU:Verenigd Koninkrijk (zonder Noord-Ierland)
              XK:Kosovo
              YU:Yugoslavia
              ZR:Zaire
              XN:Nordic Patent Institute
              null""");



        log.info("Valid countries: {}", valid.stream().map(c -> c.getCode() + ":" + c.getName(Locales.NETHERLANDISH)).collect(Collectors.joining("\n")));
        assertThat(valid).hasSize(287);

    }

    @Test
    public void testRelationValidation() {
        Relation r = new Relation(new RelationDefinition("AAAA", "a", "a"));
        r.setUriRef(":");
        Program p = new Program(AVType.AUDIO, ProgramType.BROADCAST);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        p.setType(ProgramType.CLIP);
        p.addRelation(r);

        Set<ConstraintViolation<Program>> constraintViolations = validate(p, false);
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("moet geldige url bevatten (':' is dat niet)");
        log.info("{}", constraintViolations);
    }

    @Test
    public void testWebsiteValidation() {
        Program p = new Program();
        p.setMid("mid_123");
        p.addGenre(new Genre("3.0.1.1.4"));
        p.setType(ProgramType.BROADCAST);
        p.addWebsite(new Website("bla"));
        p.setAVType(AVType.AUDIO);
        p.setAgeRating(AgeRating.ALL);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        validate(p, true, 1);
        {
            Set<ConstraintViolation<Program>> constraintViolations = dbValidate(p);
            assertThat(constraintViolations).hasSize(0);
        }
        p.getWebsites().get(0).setUrl("http://bla");
        validate(p, true, 1);
        p.getWebsites().get(0).setUrl("http://bla.nl");
        validate(p, true, 0);

        p.getWebsites().get(0).setUrl("www.kro-ncrv.nl/kruispunt");
        validate(p, true, 1);
    }

    @Test
    public void testHasGenreValidation() {
        Program p = new Program();
        p.setType(ProgramType.STRAND);
        assertThat(p.getMediaType().requiresGenre()).isFalse();
        p.setAVType(AVType.AUDIO);
        p.setAgeRating(AgeRating.ALL);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        validate(p, true, 0);

        p.setType(ProgramType.BROADCAST);

        validate(p, true, 1);

        p.addGenre(new Genre("3.0.1.1.4"));

        validate(p, true, 0);

    }


    @Test
    public void getAVTypeValidation() {
        Program p = new Program();
        p.setMid("mid_123");
        p.setType(ProgramType.TRACK);
        p.setAVType(AVType.AUDIO);
        p.setAgeRating(AgeRating.ALL);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        validate(p, true, 0);

        p.setAVType(AVType.MIXED);
        dbValidate(p, 1);
    }



    /**
     * @see #testWebsiteValidationProperty()
     */
    @Test
    @Disabled
    public void testTwitterRefValidationProperty() {
        Program p = new Program();
        p.getTwitterRefs().add(new TwitterRef("aa"));
        Set<ConstraintViolation<Program>> validate = validate(p, true);
        assertThat(validate.stream().filter(c -> c.getPropertyPath().toString().equals("twitterRefs[0].value"))).hasSize(1);
        {
            Set<ConstraintViolation<Program>> constraintViolations = validateProperty(p, "twitterRefs", true);
            assertThat(constraintViolations).hasSize(1);
            assertThat(constraintViolations.iterator().next().getMessageTemplate()).isEqualTo("{nl.vpro.constraints.Email.message}");
        }
    }



    @Test
    public void testEmailValidation() {
        Program p = new Program();
        p.setType(ProgramType.BROADCAST);
        p.setAVType(AVType.VIDEO);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);

        p.getEmail().add(new Email("bla"));
        getValidator(Locales.NETHERLANDISH).validateValue(Email.class, "email", "bla");
        Set<ConstraintViolation<Program>> constraintViolations = validate(p, false);
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getMessageTemplate()).isEqualTo("{nl.vpro.constraints.Email.message}");
        assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("'bla' is geen goed email-adres");
    }

    @Test
    @Disabled("""
    This seems to work by chance, because email contains the validatable property 'email'.
    Honestly, it seems like a bug in hibernate validator.
    """)
    public void testEmailPropertyValidation() {
        Program p = new Program();
        p.getEmail().add(new Email("bla"));
         Set<ConstraintViolation<Program>> constraintViolations = validateProperty(p, "email", true);
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getMessageTemplate()).isEqualTo("{nl.vpro.constraints.Email.message}");
    }

    @Test
    @Disabled("""
        This one than does fail.
        Honestly, it seems like a bug in hibernate validator.
        (https://hibernate.atlassian.net/browse/HV-1791?)
    """)
    public void testWebsiteValidationProperty() {
        Program p = new Program();
        p.getWebsites().add(new Website("bla"));
        {
            Set<ConstraintViolation<Program>> constraintViolations = validateProperty(p, "websites", true);
            assertThat(constraintViolations).hasSize(1);
            assertThat(constraintViolations.iterator().next().getMessageTemplate()).isEqualTo("{nl.vpro.constraints.Email.message}");
        }
    }


    @Test
    public void sortDate() {
        Program program = new Program();
        assertThat(program.getSortInstant()).isCloseTo(
            instant(), within(10, ChronoUnit.SECONDS));

        Instant publishDate = Instant.ofEpochMilli(1344043500362L);
        program.setPublishStartInstant(publishDate);
        assertThat(program.getSortInstant()).isEqualTo(publishDate);
        ScheduleEvent se = new ScheduleEvent();
        se.setStartInstant(Instant.ofEpochMilli(1444043500362L));
        program.addScheduleEvent(se);
        assertThat(program.getSortInstant()).isEqualTo(se.getStartInstant());
        Segment segment = new Segment();
        program.addSegment(segment);
        assertThat(segment.getSortInstant()).isEqualTo(se.getStartInstant());
    }

    @Test
    public void testAddLocationOnDuplicates() {
        Location l1 = new Location("http://TEST_URL", OwnerType.NEBO);
        l1.setAvAttributes(new AVAttributes(100000, AVFileFormat.WM));

        Location l2 = new Location("http://TEST_URL", OwnerType.NEBO);
        l2.setAvAttributes(new AVAttributes(110000, AVFileFormat.H264));

        Program p = MediaBuilder.program().build();
        p.addLocation(l1);
        p.addLocation(l2);

        Assertions.assertThat(p.getLocations()).hasSize(1);
        Assertions.assertThat(p.getLocations().first().getBitrate()).isEqualTo(110000);
    }

    @Test
    public void testAddLocationOnDuplicatesCollisions() {
        assertThatThrownBy(() -> {
            Location l1 = new Location("https://TEST_URL", OwnerType.NEBO);
            l1.setAvAttributes(new AVAttributes(100000, AVFileFormat.WM));

            Location l2 = new Location("https://TEST_URL", OwnerType.MIS);
            l2.setAvAttributes(new AVAttributes(110000, AVFileFormat.H264));

            Program p = MediaBuilder.program().build();
            p.addLocation(l1);
            p.addLocation(l2);

            Assertions.assertThat(p.getLocations()).hasSize(1);
            Assertions.assertThat(p.getLocations().first().getBitrate()).isEqualTo(110000);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testAddTwoLocationsWithSameAuthorityRecords() {
        Program program = new Program(1L);


        Location l1 = new Location("https://aaa", OwnerType.BROADCASTER);
        Location l2 = new Location("http://bbb", OwnerType.BROADCASTER);

        program.addLocation(l1);
        program.addLocation(l2);

        l1.setPlatform(Platform.INTERNETVOD);
        l2.setPlatform(Platform.INTERNETVOD);



        assertThat(program.getLocations()).hasSize(2);

        Prediction record = program.getPrediction(Platform.INTERNETVOD);
        assertThat(record).isNotNull();
        assertThat(record).isSameAs(program.getLocations().first().getPrediction());
        assertThat(record).isSameAs(program.getLocations().last().getPrediction());
    }


    @Test
    public void testAddLocationsOnlyUpdateCeresPredictions() {
        Location l1 = new Location("http://aaa", OwnerType.BROADCASTER);

        Program target = new Program(1L);


        target.addLocation(l1);

        Prediction plus = target.getPrediction(Platform.PLUSVOD);
        assertThat(plus).isNull();
    }

    @Test
    public void testAddLocationsOnlyUpdatePlatformPredictions() {
        Program target = new Program(1L);
        Location l1 = new Location("http://aaa", OwnerType.BROADCASTER);

        target.addLocation(l1);

        l1.setPlatform(Platform.PLUSVOD);

        Prediction plus = target.getPrediction(Platform.PLUSVOD);
        assertThat(plus.getPlatform()).isEqualTo(Platform.PLUSVOD); // used to be 'isNull' but I don't understand that.
    }

    @Test
    // MSE-2313
    public void testSilentlyFixStateOfPredictionIfLocationsAndOnlyAnnounced() {
        Location l1 = new Location("http://aaa.a/a", OwnerType.BROADCASTER);
        l1.setPlatform(Platform.INTERNETVOD);

        Program program = new Program(1L);
        program.setWorkflow(Workflow.PUBLISHED);


        program.addLocation(l1);
        program.setPredictions(Arrays.asList(Prediction.announced(Platform.INTERNETVOD).build()));

        assertThat(program.getPrediction(Platform.INTERNETVOD).getState()).isEqualTo(Prediction.State.REALIZED);
        assertThat(program.getRepubReason()).isEqualTo("realized prediction INTERNETVOD");

    }

    @Test
    // MSE-2313
    public void testDontSilentlyFixStateOfPredictionIfLocationsAndOnlyAnnounced() {
        Location l1 = new Location("http://aaa", OwnerType.BROADCASTER);
        l1.setPlatform(Platform.PLUSVOD);

        Program program = new Program(1L);


        program.addLocation(l1);

        program.setPredictions(Arrays.asList(new Prediction(Platform.INTERNETVOD, Prediction.State.ANNOUNCED)));

        assertThat(program.getPrediction(Platform.INTERNETVOD).getState())
            .isEqualTo(Prediction.State.ANNOUNCED);
    }


    @Test
    public void testAddLocationsOnPredictionUpdate() {
        Program target = new Program(1L);
        target.findOrCreatePrediction(Platform.PLUSVOD).setPlannedAvailability(true);

        Location l1 = new Location("http://aaa", OwnerType.BROADCASTER);
        l1.setAvFileFormat(AVFileFormat.HASP);
        l1.setPlatform(Platform.PLUSVOD);
        l1.setPublishStartInstant(Instant.ofEpochMilli(5));
        l1.setPublishStopInstant(Instant.ofEpochMilli(10));

        target.addLocation(l1);


        Prediction plus = target.getPrediction(Platform.PLUSVOD);
        assertThat(plus).isNotNull();
        assertThat(plus.isPlannedAvailability()).isTrue();
        assertThat(plus.getState()).isEqualTo(Prediction.State.REVOKED);
        assertThat(plus.getPublishStartInstant()).isNull();
        assertThat(plus.getOwnPublishStopInstant()).isNull();
        assertThat(plus.getPublishStopInstant()).isEqualTo(Instant.ofEpochMilli(10));

        // set also constraint on prediction, but wider
        target.getPrediction(Platform.PLUSVOD).setPublishStopInstant(Instant.ofEpochMilli(4));
        target.getPrediction(Platform.PLUSVOD).setPublishStopInstant(Instant.ofEpochMilli(11));

        assertThat(l1.getPublishStartInstant().toEpochMilli()).isEqualTo(5);
        assertThat(l1.getPublishStopInstant().toEpochMilli()).isEqualTo(10);

        // now try narrower
        target.getPrediction(Platform.PLUSVOD).setPublishStartInstant(Instant.ofEpochMilli(6));
        target.getPrediction(Platform.PLUSVOD).setPublishStopInstant(Instant.ofEpochMilli(9));

        assertThat(l1.getPublishStartInstant().toEpochMilli()).isEqualTo(6);
        assertThat(l1.getPublishStopInstant().toEpochMilli()).isEqualTo(9);

    }

    @Test
    public void testSortDateWithScheduleEvents() {
        final Program program = MediaBuilder.program()
            .creationInstant(Instant.ofEpochMilli(1))
            .publishStart(Instant.ofEpochMilli(2))
            .scheduleEvents(
                ScheduleEvent.builder().channel(Channel.NED2).start(Instant.ofEpochMilli(13)).duration(java.time.Duration.ofMillis(10)).rerun(true).build(),
                ScheduleEvent.builder().channel(Channel.NED1).start(Instant.ofEpochMilli(3)).duration(java.time.Duration.ofMillis(10)).build()
            )
            .build();

        assertThat(program.getSortInstant()).isEqualTo(Instant.ofEpochMilli(3));
    }

    @Test
    public void testSortDateWithPublishStart() {
        final Program program = MediaBuilder.program()
            .creationInstant(Instant.ofEpochMilli(1))
            .publishStart(Instant.ofEpochMilli(2))
            .build();

        assertThat(program.getSortInstant()).isEqualTo(Instant.ofEpochMilli(2));
    }

    @Test
    public void testSortDateWithCreationDate() {
        final Program program = MediaBuilder.program()
            .creationInstant(Instant.ofEpochMilli(1))
            .build();

        assertThat(program.getSortInstant()).isEqualTo(Instant.ofEpochMilli(1));
    }



    @Test
    public void testRealizePrediction() {
        final Program program = MediaBuilder.program()
            .id(1L)
            .build();


        final Location location1 = new Location("http://bla/1", OwnerType.BROADCASTER);
        location1.setPlatform(Platform.INTERNETVOD);
        program.addLocation(location1);

        program.realizePrediction(location1);

        final Location location2 = new Location("http://bla/2", OwnerType.BROADCASTER);
        location2.setPlatform(Platform.INTERNETVOD);
        program.addLocation(location2);




        assertThat(program.getPrediction(Platform.INTERNETVOD).getState()).isEqualTo(Prediction.State.REALIZED);
        assertThat(program.getPrediction(Platform.INTERNETVOD).getPublishStartInstant()).isNull();
        assertThat(program.getPrediction(Platform.INTERNETVOD).getPublishStopInstant()).isNull();


    }

    @Test
    public void testUnmarshal() {
        final Program program = MediaBuilder.program()
            .id(1L)
            .build();

        Prediction unPlanned = program.findOrCreatePrediction(Platform.INTERNETVOD);
        unPlanned.setState(Prediction.State.ANNOUNCED);
        assertThat(unPlanned.isPlannedAvailability()).isFalse();


        Program result = JAXBTestUtil.roundTrip(program);


        assertThat(result.getPrediction(Platform.INTERNETVOD)).isNull(); // it's not available , so status is irrelevant, and not exposed.

        program.findOrCreatePrediction(Platform.INTERNETVOD).setPlannedAvailability(true);

        result = JAXBTestUtil.roundTrip(program);


        assertThat(result.getPrediction(Platform.INTERNETVOD)).isNotNull();
        assertThat(result.getPrediction(Platform.INTERNETVOD).getState()).isEqualTo(Prediction.State.ANNOUNCED);
        assertThat(result.getPrediction(Platform.INTERNETVOD).isPlannedAvailability()).isTrue();


    }

    @Test
    public void testHash() {
        final Program program = MediaBuilder.program()
            .lastModified(instant())
            .creationInstant(Instant.ofEpochMilli(10000))
            .lastPublished(instant())
            .id(1L)
            .build();
        program.acceptChanges();
        byte[] bytes = program.serializeForCalcCRC32();
        assertThat(new String(bytes)).isEqualTo("""
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <program embeddable="true" sortDate="1970-01-01T01:00:10+01:00"  creationDate="1970-01-01T01:00:10+01:00" urn="urn:vpro:media:program:1" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009">
                <credits/>
                <locations/>
                <images/>
                <scheduleEvents/>
                <segments/>
            </program>
            """);

        assertThat(program.getHash()).isEqualTo(2081675751L);
    }


    @Test
    public void testHasChanges() {
        final Program program = MediaBuilder.program()
            .lastModified(instant())
            .lastPublished(instant())
            .id(1L)
            .build();

        assertThat(program.hasChanges()).isTrue();
        program.acceptChanges();
        assertThat(program.hasChanges()).isFalse();
        program.setPublishStartInstant(instant());
        assertThat(program.hasChanges()).isTrue();
        program.acceptChanges();
        assertThat(program.hasChanges()).isFalse();
    }

    @Test
    public void testSetWorkflowWhenMerged() {
        assertThatThrownBy(() -> {
            final Program merged = new Program();

            merged.setMergedTo(new Group());
            merged.setWorkflow(Workflow.PUBLISHED);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testFindAncestry() {
        final Group grandParent = MediaBuilder.group().titles(new Title("Grand parent", OwnerType.BROADCASTER, TextualType.MAIN)).build();
        final Program parent = MediaBuilder.program().titles(new Title("Parent", OwnerType.BROADCASTER, TextualType.MAIN)).memberOf(grandParent, 1).build();
        final Program child = MediaBuilder.program().titles(new Title("Child", OwnerType.BROADCASTER, TextualType.MAIN)).memberOf(parent, 1).build();

        final List<MediaObject> ancestry = child.findAncestry(grandParent);
        assertThat(ancestry).hasSize(2);
        assertThat(ancestry.get(0)).isSameAs(grandParent);
        assertThat(ancestry.get(1)).isSameAs(parent);
    }

    @Test
    public void testAddImageWithMultipleOwners() {
        Image imgn1 = Image.builder().imageUri("urn:image:1").owner(NEBO).build();
        Image imgn2 = Image.builder().imageUri("urn:image:2").owner(NEBO).build();
        Image imgn3 = Image.builder().imageUri("urn:image:3").owner(NEBO).build();

        Image imgc1 = Image.builder().imageUri("urn:image:ceres1").owner(CERES).crid("crid://ceres/1").build();
        Image imgc2 = Image.builder().imageUri("urn:image:ceres2").owner(CERES).build();
        Image imgc3 = Image.builder().imageUri("urn:image:ceres3").owner(CERES).build();

        Program existing = MediaBuilder.program().images(
            imgn1, imgn2, //nebo
            imgc1, imgc2, imgc3 // ceres
        ).build();


        // incoming has ceres images too, this is odd, but they will be ignored
        Program incoming = MediaBuilder.program().images(
            imgn3, imgn1, imgn2, // nebo, added one and ordered
            imgc1, imgc3 // other images should be ignored
        ).build();

        existing.mergeImages(incoming, NEBO); // because we are nebo, and should not have shipped any thing different

        // arrived and in correct order
        assertEquals("urn:image:3", existing.getImages().get(0).getImageUri());
        assertEquals("urn:image:1", existing.getImages().get(1).getImageUri());
        assertEquals("urn:image:2", existing.getImages().get(2).getImageUri());

        // other images remain untouched and in the same order
        // other images remain untouched and in same  order
        assertEquals("urn:image:ceres1", existing.getImages().get(3).getImageUri());
        assertEquals("urn:image:ceres2", existing.getImages().get(4).getImageUri());
        assertEquals("urn:image:ceres3", existing.getImages().get(5).getImageUri());
    }


    @Test
    public void mergeImagesAnother() {
        Image old1 = Image.builder().imageUri("urn:image:1").owner(AUTHORITY).build();
        Image old2 = Image.builder().imageUri("urn:image:2").owner(AUTHORITY).build();
        Image old3 = Image.builder().imageUri("urn:image:3").owner(AUTHORITY).build();

        Image new1 = Image.builder().imageUri("urn:image:1").owner(AUTHORITY).build();
        Image new2 = Image.builder().imageUri("urn:image:2").owner(AUTHORITY).build();
        Image new3 = Image.builder().imageUri("urn:image:3").owner(AUTHORITY).build();

        Program existing = MediaBuilder.program().build();

        Program incoming = MediaBuilder.program().images(
            new1,
            old1,
            new2,
            new3
        ).build();

        existing.mergeImages(incoming, AUTHORITY);

        // arrived and in correct order
        assertThat(existing.getImages()).hasSize(4);
        assertThat(existing.getImages().get(0).getImageUri()).isEqualTo("urn:image:1");
        assertThat(existing.getImages().get(1).getImageUri()).isEqualTo("urn:image:1");
        assertThat(existing.getImages().get(2).getImageUri()).isEqualTo("urn:image:2");
        assertThat(existing.getImages().get(3).getImageUri()).isEqualTo("urn:image:3");

    }


    @Test
    public void testMergeImagesChange() {
        Image existingImage1 = Image.builder().imageUri("urn:image:1").owner(BROADCASTER).title("Before title").build();
        Image existingImage2 = Image.builder().imageUri("urn:image:2").owner(BROADCASTER).crid("crid://existing/2").build();
        Image existingImage3= Image.builder().imageUri("urn:image:ceres1").owner(CERES).build();


        Image incomingImage1 = Image.builder().imageUri("urn:image:1").owner(BROADCASTER).title("Updated title").build();
        Image incomingImage2 = Image.builder().imageUri("urn:image:2").owner(BROADCASTER).crid("crid://incoming/1").build();

        Program existing = MediaBuilder.program().images(
            existingImage1, existingImage2,
            null, // Null in existing?
            existingImage3
        ).build();

        // incoming has ceres images too, and even a null,  this is odd, but they will be ignored
        Program incoming = MediaBuilder.program().images(
            incomingImage2,
            null,
            incomingImage1
        ).build();

        existing.mergeImages(incoming, BROADCASTER);

        // arrived and in correct order
        assertEquals("urn:image:2", existing.getImages().get(0).getImageUri());
        assertEquals("urn:image:1", existing.getImages().get(1).getImageUri());
        assertEquals("urn:image:ceres1", existing.getImages().get(2).getImageUri());

        // fields are updated too
        assertThat(existing.getImages().get(0).getCrids()).containsExactly("crid://incoming/1");
        assertThat(existing.getImages().get(1).getTitle()).isEqualTo("Updated title");

    }


    @Test
    @Disabled
    public void testMergeImagesExistingForDifferentOwner() {
        Image existingImage1 = Image.builder().imageUri("urn:image:1").owner(BROADCASTER).title("broadcaster owner").build();
        Image existingImage2 = Image.builder().imageUri("urn:image:2").owner(RADIOBOX).title("radiobox owner").build();

        Image incomingImage1 = Image.builder().imageUri("urn:image:1").owner(RADIOBOX).title("broadcaster owner updated by radiobox").build();
        Image incomingImage2 = Image.builder().imageUri("urn:image:2").owner(BROADCASTER).title("radiobox owner updated by broadcaster").build();

        Program existing = MediaBuilder.program().images(
            existingImage1,
            existingImage2
        ).build();

        Program incoming = MediaBuilder.program().images(
            incomingImage2, // ignored, since of wrong owner
            incomingImage1
        ).build();

        existing.mergeImages(incoming, BROADCASTER);

        // arrived and in correct order
        assertThat(existing.getImages().get(0).getImageUri()).isEqualTo("urn:image:2");
        assertThat(existing.getImages().get(1).getImageUri()).isEqualTo("urn:image:1");
        assertThat(existing.getImages().get(1).getOwner()).isEqualTo(RADIOBOX);

        // fields are updated too
        assertThat(existing.getImages().get(1).getTitle()).isEqualTo("Updated title");
    }


    @Test
    public void addLocationToProgramWithSystemAuthorizedPrediction() {
        Program program = MediaBuilder.program().build();
        Prediction prediction = new Prediction(Platform.INTERNETVOD);
        prediction.setAuthority(Authority.SYSTEM);
        prediction.setPublishStartInstant(instant());
        program.setPredictions(Arrays.asList(prediction));
        Location l1 = new Location("https://TEST_URL/foo.bar", OwnerType.AUTHORITY);
        l1.setPlatform(Platform.INTERNETVOD);
        program.addLocation(l1);
        assertNotNull(program.getLocation(l1));
    }


    @Test
    public void testMemberOf() {
        Group group = JAXB.unmarshal(new StringReader("""
            <group xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" isOrdered="true" type="SEASON" avType="VIDEO" embeddable="true" mid="VPWON_1240914" sortDate="2016-09-15T09:15:00+02:00" workflow="PUBLISHED" creationDate="2015-02-18T06:51:59.964+01:00" lastModified="2016-12-21T11:20:37.369+01:00" publishDate="2016-12-21T11:23:53.445+01:00" urn="urn:vpro:media:group:51613423">
            <broadcaster id="VPRO">VPRO</broadcaster>
            <title owner="BROADCASTER" type="MAIN">VPRO Tegenlicht 2015 (HH)</title>
            <title owner="MIS" type="MAIN">VPRO Tegenlicht</title>
            <title owner="WHATS_ON" type="MAIN">VPRO Tegenlicht</title>
            <title owner="BROADCASTER" type="SUB">Seizoen 2015 (HH)</title>
            <description owner="MIS" type="MAIN">
            VPRO Tegenlicht speurt in binnen- en buitenland naar ontwikkelingen in de politiek, economie, maatschappij en wetenschap die onze nabije toekomst zullen bepalen.
            </description>
            <releaseYear>2015</releaseYear>
            <credits/>
            <descendantOf urnRef="urn:vpro:media:group:45760423" midRef="POMS_S_VPRO_652484" type="COLLECTION"/>
            <descendantOf urnRef="urn:vpro:media:group:58901677" midRef="POMS_S_VPRO_1405375" type="COLLECTION"/>
            <memberOf added="2015-08-14T14:02:59.793+02:00" highlighted="false" midRef="POMS_S_VPRO_1405375" index="5" type="COLLECTION" urnRef="urn:vpro:media:group:58901677"/>
            <locations/>
            <scheduleEvents/>
            <images/>
            </group>"""), Group.class);

        Group owner = new Group(0L);
        owner.setMid("POMS_S_VPRO_1405375");
        assertThat(group.isMemberOf(owner)).isTrue();

    }

    @Test
    @Disabled("TODO: Resulting segment cannot be unmarshalled")
    public void oddChars() {
        Segment mo = new Segment();
        mo.setMainDescription("In de Abdijkerk in het Groningse Aduard staat vanavond de smaak van Emmy Verhey centraal. Op het programma: Messiaen, Ravel, Dvorak en Loevendie. Nata Tsvereli, Christophe Weidmann en Amparo Lacruz zijn van de partij, evenals pianist Paul Komen. Hem spreken wij vlak voor aanvang.");

        Set<ConstraintViolation<Segment>> constraintViolations = getValidator().validate(mo);
        log.info("{}", constraintViolations);

        Segment rounded = JAXBTestUtil.roundTrip(mo);
        JAXB.marshal(mo, System.out);
        log.info("{}", rounded.getMainDescription());

    }

}
