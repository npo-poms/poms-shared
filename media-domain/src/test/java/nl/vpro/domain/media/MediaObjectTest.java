/**
 * Copyright (C) 2008 All rights reserved VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.util.*;

import javax.validation.ConstraintViolation;

import org.assertj.core.api.Assertions;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static nl.vpro.domain.media.MediaDomainTestHelper.validator;
import static org.assertj.core.api.Assertions.assertThat;

public class MediaObjectTest {

    @BeforeClass
    public static void setup() {
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);
    }

    @Test
    public void testIdFromUrn() {
        assertThat(Program.idFromUrn("urn:vpro:media:program:12463402")).isEqualTo(12463402L);
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
    public void testAddTitle() {
        MediaObject mediaObject = new Program();
        mediaObject.addTitle(null);
        assertThat(mediaObject.getTitles()).isEmpty();

        mediaObject.addTitle(new Title("Title 1", OwnerType.MIS, TextualType.MAIN));
        assertThat(mediaObject.getTitles().first().getParent()).isEqualTo(mediaObject);
        assertThat(mediaObject.getTitles()).hasSize(1);
        assertThat(mediaObject.getTitles().last().getTitle()).isEqualTo("Title 1");

        mediaObject.addTitle(new Title("Title 2", OwnerType.MIS, TextualType.EPISODE));
        assertThat(mediaObject.getTitles()).hasSize(2);
        assertThat(mediaObject.getTitles().last().getTitle()).isEqualTo("Title 2");

        mediaObject.addTitle(new Title("Title 3", OwnerType.MIS, TextualType.EPISODE));
        assertThat(mediaObject.getTitles()).hasSize(2);
        assertThat(mediaObject.getTitles().last().getTitle()).isEqualTo("Title 3");
    }

    @Test
    public void testAddDescription() {
        MediaObject mediaObject = new Program();
        mediaObject.addDescription(null);
        assertThat(mediaObject.getDescriptions()).isEmpty();

        mediaObject.addDescription(new Description("Des 1", OwnerType.MIS, TextualType.MAIN));
        assertThat(mediaObject.getDescriptions().first().getParent()).isEqualTo(mediaObject);
        assertThat(mediaObject.getDescriptions()).hasSize(1);
        assertThat(mediaObject.getDescriptions().last().getDescription()).isEqualTo("Des 1");

        mediaObject.addDescription(new Description("Des 2", OwnerType.MIS, TextualType.EPISODE));
        assertThat(mediaObject.getDescriptions()).hasSize(2);
        assertThat(mediaObject.getDescriptions().last().getDescription()).isEqualTo("Des 2");

        mediaObject.addDescription(new Description("Des 3", OwnerType.MIS, TextualType.EPISODE));
        assertThat(mediaObject.getDescriptions()).hasSize(2);
        assertThat(mediaObject.getDescriptions().last().getDescription()).isEqualTo("Des 3");
    }

    @Test
    public void testGetAncestors() throws CircularReferenceException {
        Program program = new Program();
        Group group1 = new Group(GroupType.PLAYLIST);
        Group group2 = new Group(GroupType.PLAYLIST);
        Group root = new Group(GroupType.PLAYLIST);

        program.createMemberOf(group1, 1);
        group1.createMemberOf(group2, 1);
        group2.createMemberOf(root, 1);

        SortedSet<MediaObject> ancestors = program.getAncestors();

        assertThat(ancestors).hasSize(3);
    }

    @Test
    public void testGetAncestorsForUniqueReferences() throws CircularReferenceException {
        Program program = new Program();
        Group group1 = new Group(GroupType.PLAYLIST);
        Group group2 = new Group(GroupType.PLAYLIST);
        Group root = new Group(GroupType.PLAYLIST);

        program.createMemberOf(group1, 1);
        program.createMemberOf(group2, 2);
        group1.createMemberOf(root, 1);
        group2.createMemberOf(root, 2);

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

        program.createMemberOf(group1, 1);
        program.createMemberOf(group2, 2);
        group1.createMemberOf(root, 1);
        group2.createMemberOf(root, 2);

        SortedSet<MediaObject> ancestors = program.getAncestors();

        assertThat(ancestors).hasSize(3);
    }

    @Test(expected = CircularReferenceException.class)
    public void testCreateMemberOfForSelf() throws CircularReferenceException {
        Group g1 = new Group();

        g1.createMemberOf(g1, 1);
    }

    @Test(expected = CircularReferenceException.class)
    public void testCreateMemberOfForCircularity() throws CircularReferenceException {
        Group g1 = new Group(GroupType.PLAYLIST);
        Group g2 = new Group(GroupType.PLAYLIST);
        Group g3 = new Group(GroupType.PLAYLIST);
        Group g4 = new Group(GroupType.PLAYLIST);

        g1.createMemberOf(g2, 1);
        g2.createMemberOf(g3, 1);
        g3.createMemberOf(g4, 1);
        g4.createMemberOf(g1, 1);

        //assertThat(g1.getAncestors()).hasSize(4);
    }

    protected Program getTestProgram() throws CircularReferenceException {
        Program program = new Program(1L);
        program.setPredictions(Arrays.asList(new Prediction(Platform.INTERNETVOD)));
        program.setUrn("urn:vpro:media:program:123");
        program.setCreationDate(new Date(0));
        Title t = new Title("bla", OwnerType.BROADCASTER, TextualType.MAIN);
        program.addTitle(t);
        program.addDescription("bloe", OwnerType.BROADCASTER, TextualType.MAIN);

        Group group = new Group();
        group.setUrn("urn:vpro:media:group:122");
        program.addBroadcaster(new Broadcaster("VPRO", "V.P.R.O"));
        MemberRef ref = group.createMember(program, 1);
        ref.setHighlighted(true);
        ref.setAdded(new Date(0));

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
    public void testProgramValidation() throws Exception {
        Program p = new Program();
        p.setType(ProgramType.BROADCAST);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        Set<ConstraintViolation<Program>> constraintViolations = validator.validate(p);
        assertThat(constraintViolations).hasSize(1);
    }

    @Test
    public void testMidValidation() throws Exception {
        Program p = new Program();
        p.setType(ProgramType.BROADCAST);
        p.setAVType(AVType.MIXED);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        p.setMid("foo/bar");
        Set<ConstraintViolation<Program>> constraintViolations = validator.validate(p);
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getMessageTemplate()).isEqualTo("{nl.vpro.constraints.mid}");
    }


    @Test
    public void testRelationValidation() throws Exception {
        Relation r = new Relation(new RelationDefinition("AAAA", "a", "a"));
        r.setUriRef(":");
        Program p = new Program(AVType.AUDIO, ProgramType.BROADCAST);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        p.setType(ProgramType.CLIP);
        p.addRelation(r);

        Set<ConstraintViolation<Program>> constraintViolations = validator.validate(p);
        assertThat(constraintViolations).hasSize(1);
    }

    @Test
    public void sortDate() {
        Program program = new Program();
        assertThat(Math.abs(program.getSortDate().getTime() - System.currentTimeMillis())).isLessThan(10000);
        Date publishDate = new Date(1344043500362L);
        program.setPublishStart(publishDate);
        assertThat(program.getSortDate()).isEqualTo(publishDate);
        ScheduleEvent se = new ScheduleEvent();
        se.setStartInstant(new Date(1444043500362L).toInstant());
        program.addScheduleEvent(se);
        assertThat(program.getSortDate()).isEqualTo(se.getStart());
        Segment segment = new Segment();
        program.addSegment(segment);
        assertThat(segment.getSortDate()).isEqualTo(se.getStart());
    }

    @Test
    public void testAddLocationOnDuplicates() {
        Location l1 = new Location("TEST_URL", OwnerType.NEBO);
        l1.setAvAttributes(new AVAttributes(100000, AVFileFormat.WM));

        Location l2 = new Location("TEST_URL", OwnerType.NEBO);
        l2.setAvAttributes(new AVAttributes(110000, AVFileFormat.H264));

        Program p = MediaBuilder.program().build();
        p.addLocation(l1);
        p.addLocation(l2);

        Assertions.assertThat(p.getLocations()).hasSize(1);
        Assertions.assertThat(p.getLocations().first().getBitrate()).isEqualTo(110000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddLocationOnDuplicatesCollisions() {
        Location l1 = new Location("TEST_URL", OwnerType.NEBO);
        l1.setAvAttributes(new AVAttributes(100000, AVFileFormat.WM));

        Location l2 = new Location("TEST_URL", OwnerType.MIS);
        l2.setAvAttributes(new AVAttributes(110000, AVFileFormat.H264));

        Program p = MediaBuilder.program().build();
        p.addLocation(l1);
        p.addLocation(l2);

        Assertions.assertThat(p.getLocations()).hasSize(1);
        Assertions.assertThat(p.getLocations().first().getBitrate()).isEqualTo(110000);
    }

    @Test
    public void testAddTwoLocationsWithSameAuthorityRecords() throws Exception {
        Program program = new Program(1L);
        LocationAuthorityRecord.authoritative(program, Platform.INTERNETVOD);

        Location l1 = new Location("aaa", OwnerType.BROADCASTER);
        Location l2 = new Location("bbb", OwnerType.BROADCASTER);

        program.addLocation(l1);
        program.addLocation(l2);

        l1.setPlatform(Platform.INTERNETVOD);
        l2.setPlatform(Platform.INTERNETVOD);



        assertThat(program.getLocations()).hasSize(2);

        LocationAuthorityRecord record = program.getLocationAuthorityRecord(Platform.INTERNETVOD);
        assertThat(record).isNotNull();
        assertThat(record).isSameAs(program.getLocations().first().getAuthorityRecord());
        assertThat(record).isSameAs(program.getLocations().last().getAuthorityRecord());
    }


    @Test
    public void testAddLocationsOnlyUpdateCeresPredictions() throws Exception {
        Location l1 = new Location("aaa", OwnerType.BROADCASTER);

        Program target = new Program(1L);
        LocationAuthorityRecord.nonAuthoritative(target, Platform.PLUSVOD);

        target.addLocation(l1);

        Prediction plus = target.getPrediction(Platform.PLUSVOD);
        assertThat(plus).isNull();
    }

    @Test
    public void testAddLocationsOnlyUpdatePlatformPredictions() throws Exception {
        Program target = new Program(1L);
        Location l1 = new Location("aaa", OwnerType.BROADCASTER);
        LocationAuthorityRecord.authoritative(target, Platform.PLUSVOD);
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
        LocationAuthorityRecord.authoritative(program, Platform.INTERNETVOD);

        program.addLocation(l1);

        program.setPredictions(Arrays.asList(new Prediction(Platform.INTERNETVOD, Prediction.State.ANNOUNCED)));

        assertThat(program.getPrediction(Platform.INTERNETVOD).getState()).isEqualTo(Prediction.State.REALIZED);
        assertThat(program.getWorkflow()).isEqualTo(Workflow.FOR_REPUBLICATION);

    }

    @Test
    // MSE-2313
    public void testDontSilentlyFixStateOfPredictionIfLocationsAndOnlyAnnounced() {
        Location l1 = new Location("http://aaa", OwnerType.BROADCASTER);
        l1.setPlatform(Platform.PLUSVOD);

        Program program = new Program(1L);
        LocationAuthorityRecord.authoritative(program, Platform.PLUSVOD);

        program.addLocation(l1);

        program.setPredictions(Arrays.asList(new Prediction(Platform.INTERNETVOD, Prediction.State.ANNOUNCED)));

        assertThat(program.getPrediction(Platform.INTERNETVOD).getState()).isEqualTo(Prediction.State.ANNOUNCED);
    }


    @Test
    public void testAddLocationsOnPredictionUpdate() throws Exception {
        Program target = new Program(1L);
        LocationAuthorityRecord.authoritative(target, Platform.PLUSVOD);

        Location l1 = new Location("aaa", OwnerType.BROADCASTER);

        target.addLocation(l1);

        l1.setPlatform(Platform.PLUSVOD);
        l1.setPublishStart(new Date(5));
        l1.setPublishStop(new Date(10));




        Prediction plus = target.getPrediction(Platform.PLUSVOD);
        assertThat(plus).isNotNull();
        assertThat(plus.getState()).isEqualTo(Prediction.State.REALIZED);
        assertThat(plus.getPublishStart()).isEqualTo(new Date(5));
        assertThat(plus.getPublishStop()).isEqualTo(new Date(10));
    }

    @Test
    public void testSortDateWithScheduleEvents() throws Exception {
        final Program program = MediaBuilder.program()
            .creationDate(new Date(1))
            .publishStart(new Date(2))
            .scheduleEvents(
                new ScheduleEvent(Channel.NED2, new Date(13), new Date(10)),
                new ScheduleEvent(Channel.NED1, new Date(3), new Date(10))
            )
            .build();

        assertThat(program.getSortDate()).isEqualTo(new Date(3));
    }

    @Test
    public void testSortDateWithPublishStart() throws Exception {
        final Program program = MediaBuilder.program()
            .creationDate(new Date(1))
            .publishStart(new Date(2))
            .build();

        assertThat(program.getSortDate()).isEqualTo(new Date(2));
    }

    @Test
    public void testSortDateWithCreationDate() throws Exception {
        final Program program = MediaBuilder.program()
            .creationDate(new Date(1))
            .build();

        assertThat(program.getSortDate()).isEqualTo(new Date(1));
    }



    @Test
    public void testRealizePrediction() {
        final Program program = MediaBuilder.program()
            .id(1L)
            .build();


        LocationAuthorityRecord.nonAuthoritative(program, Platform.INTERNETVOD);

        final Location location1 = new Location("http://bla/1", OwnerType.BROADCASTER);
        location1.setPlatform(Platform.INTERNETVOD);
        program.addLocation(location1);

        program.realizePrediction(location1);

        final Location location2 = new Location("http://bla/2", OwnerType.BROADCASTER);
        location2.setPlatform(Platform.INTERNETVOD);
        program.addLocation(location2);




        assertThat(program.getPrediction(Platform.INTERNETVOD).getState()).isEqualTo(Prediction.State.REALIZED);
        assertThat(program.getPrediction(Platform.INTERNETVOD).getPublishStart()).isNull();
        assertThat(program.getPrediction(Platform.INTERNETVOD).getPublishStop()).isNull();


    }

    @Test
    public void testUnmarshal() {
        final Program program = MediaBuilder.program()
            .id(1L)
            .build();

        LocationAuthorityRecord.nonAuthoritative(program, Platform.INTERNETVOD);
        program.updatePrediction(Platform.INTERNETVOD, Prediction.State.ANNOUNCED);

        Program result = JAXBTestUtil.roundTrip(program);

        assertThat(result.getLocationAuthorityRecord(Platform.INTERNETVOD)).isNotNull();
    }

    @Test
    public void testHash() {
        final Program program = MediaBuilder.program()
            .lastModified(new Date())
            .creationDate(new Date(10000))
            .lastPublished(new Date())
            .id(1L)
            .build();
        program.acceptChanges();
        assertThat(program.getHash()).isEqualTo(362556323L);


        program.setType(ProgramType.CLIP);
        program.acceptChanges();

        assertThat(program.getHash()).isNotEqualTo(362556323L);
    }


    @Test
    public void testHasChanges() {
        final Program program = MediaBuilder.program()
            .lastModified(new Date())
            .lastPublished(new Date())
            .id(1L)
            .build();

        assertThat(program.hasChanges()).isTrue();
        program.acceptChanges();
        assertThat(program.hasChanges()).isFalse();
        program.setPublishStart(new Date());
        assertThat(program.hasChanges()).isTrue();
        program.acceptChanges();
        assertThat(program.hasChanges()).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetWorkflowWhenMerged() throws Exception {
        final Program merged = new Program();

        merged.setMergedTo(new Group());
        merged.setWorkflow(Workflow.PUBLISHED);
    }

    @Test
    public void testFindAncestry() throws Exception {
        final Group grandParent = MediaBuilder.group().titles(new Title("Grand parent", OwnerType.BROADCASTER, TextualType.MAIN)).build();
        final Program parent = MediaBuilder.program().titles(new Title("Parent", OwnerType.BROADCASTER, TextualType.MAIN)).memberOf(grandParent, 1).build();
        final Program child = MediaBuilder.program().titles(new Title("Child", OwnerType.BROADCASTER, TextualType.MAIN)).memberOf(parent, 1).build();

        final List<MediaObject> ancestry = child.findAncestry(grandParent);
        assertThat(ancestry).hasSize(2);
        assertThat(ancestry.get(0)).isSameAs(grandParent);
        assertThat(ancestry.get(1)).isSameAs(parent);
    }
}
