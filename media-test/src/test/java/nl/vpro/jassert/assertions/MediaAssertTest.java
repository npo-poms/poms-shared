/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.jassert.assertions;

import java.time.Instant;

import org.junit.Test;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.domain.user.Broadcaster;

import static nl.vpro.domain.media.MediaTestDataBuilder.*;
import static nl.vpro.jassert.assertions.MediaAssertions.mediaAssertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 1.5
 */
@SuppressWarnings("deprecation")
public class MediaAssertTest {

    @Test(expected = AssertionError.class)
    public void testIsProgramOnNull() {
        mediaAssertThat(null).isProgram();
    }

    @Test
    public void testIsProgram() {
        mediaAssertThat((program().build())).isProgram();
    }

    @Test
    public void testIsProgramOnType() {
        mediaAssertThat((program().withType().build())).isProgram(ProgramType.BROADCAST);
    }

    @Test(expected = AssertionError.class)
    public void testIsGroupOnNull() {
        mediaAssertThat((MediaObject)null).isGroup();
    }

    @Test
    public void testIsGroup() {
        mediaAssertThat((group().build())).isGroup();
    }

    @Test
    public void testIsGroupOnType() {
        mediaAssertThat((group().withType().build())).isGroup(GroupType.PLAYLIST);
    }

    @Test(expected = AssertionError.class)
    public void testHasPoSeriesIDOnOtherClass() {
        mediaAssertThat((segment().build())).hasPoSeriesID("VPROWON_12345");
    }

    @Test(expected = AssertionError.class)
    public void testHasPoSeriesIDOnOtherId() {
        mediaAssertThat((segment().build())).hasPoSeriesID("no match");
    }

    @Test
    public void testHasPoSeriesIDOnGroup() {
        mediaAssertThat((group().withPoSeriesID().build())).hasPoSeriesID("VPRO_12345");
    }

    @Test(expected = AssertionError.class)
    public void testIsSegmentOnNull() {
        mediaAssertThat((MediaObject)null).isSegment();
    }

    @Test
    public void testIsSegment() {
        mediaAssertThat((segment().build())).isSegment();
    }

    @Test(expected = AssertionError.class)
    public void testIsVideoOnNull() {
        mediaAssertThat((MediaObject)null).isVideo();
    }

    @Test
    public void testIsVideo() {
        mediaAssertThat(program().avType(AVType.VIDEO).build()).isVideo();
    }

    @Test(expected = AssertionError.class)
    public void testIsAudioOnNull() {
        mediaAssertThat((MediaObject)null).isAudio();
    }

    @Test
    public void testIsAudio() {
        mediaAssertThat(program().avType(AVType.AUDIO).build()).isAudio();
    }

    @Test(expected = AssertionError.class)
    public void testIsMixedOnNull() {
        mediaAssertThat((MediaObject)null).isMixed();
    }

    @Test
    public void testIsMixed() {
        mediaAssertThat(program().avType(AVType.MIXED).build()).isMixed();
    }

    @Test(expected = AssertionError.class)
    public void testHasWorkflowOnNull() {
        mediaAssertThat((MediaObject)null).hasWorkflow(Workflow.PUBLISHED);
    }

    @Test(expected = AssertionError.class)
    public void testHasWorkflowOnNullArgument() {
        mediaAssertThat(program().withWorkflow().build()).hasWorkflow(null);
    }

    @Test
    public void testHasWorkflow() {
        mediaAssertThat((program().withWorkflow().build())).hasWorkflow(Workflow.PUBLISHED);
    }

    @Test(expected = AssertionError.class)
    public void testHasTitleOnNull() {
        mediaAssertThat((MediaObject)null).hasTitles();
    }

    @Test(expected = AssertionError.class)
    public void testHasBroadcasterWhenEmpty() {
        mediaAssertThat(program().build()).hasBroadcasters();
    }

    @Test
    public void testHasBroadcaster() {
        mediaAssertThat(program().withBroadcasters().build()).hasBroadcasters();
    }

    @Test
    public void testHasBroadcasterWithIds() {
        mediaAssertThat(program().withBroadcasters().build()).hasBroadcasters("AVRO");
    }

    @Test(expected = AssertionError.class)
    public void testHasOnlyBroadcasterWithIdsOnFailure() {
        mediaAssertThat(program().withBroadcasters().build()).hasOnlyBroadcasters("AVRO");
    }

    @Test
    public void testHasOnlyBroadcasterWithIds() {
        mediaAssertThat(program().withBroadcasters().build()).hasOnlyBroadcasters("AVRO", "BNN");
    }

    @Test
    public void testHasBroadcasters() {
        mediaAssertThat(program().withBroadcasters().build()).hasBroadcasters(new Broadcaster("AVRO", "AVRO"));
    }

    @Test(expected = AssertionError.class)
    public void testHasOnlyBroadcastersOnFailure() {
        mediaAssertThat(program().withBroadcasters().build()).hasOnlyBroadcasters(new Broadcaster("AVRO", "AVRO"));
    }

    @Test
    public void testHasOnlyBroadcasters() {
        mediaAssertThat(program().withBroadcasters().build()).hasOnlyBroadcasters(new Broadcaster("AVRO", "AVRO"), new Broadcaster("BNN", "BNN"));
    }

    @Test(expected = AssertionError.class)
    public void testHasTitleWhenEmpty() {
        mediaAssertThat(program().build()).hasTitles();
    }

    @Test
    public void testHasTitle() {
        mediaAssertThat(program().withTitles().build()).hasTitles();
    }

    @Test
    public void testHasTitleWithOwnerAndType() {
        mediaAssertThat(program().withTitles().build()).hasTitle(OwnerType.BROADCASTER, TextualType.SHORT);
    }

    @Test(expected = AssertionError.class)
    public void testHasTitleForTextAndTypeOnFailure() {
        mediaAssertThat((program().withTitles().build())).hasTitle("Main title", TextualType.SUB);
    }

    public void testHasTitleForTextAndType() {
        mediaAssertThat((program().withTitles().build())).hasTitle("Main title", TextualType.MAIN);
    }

    @Test(expected = AssertionError.class)
    public void testHasTitleForTextAndOwnerOnFailure() {
        mediaAssertThat((program().withTitles().build())).hasTitle("Main title", OwnerType.MIS);
    }

    public void testHasTitleForTextAndOwner() {
        mediaAssertThat((program().withTitles().build())).hasTitle("Main title", OwnerType.BROADCASTER);
    }

    @Test
    public void testHasTitleForAllOwners() {
        mediaAssertThat((program().withTitles().build())).hasTitle(OwnerType.BROADCASTER, OwnerType.MIS);
    }

    @Test(expected = AssertionError.class)
    public void testHasTitleOnMissingOwner() {
        mediaAssertThat((program().withTitles().build())).hasTitle(OwnerType.CERES);
    }

    @Test
    public void testHasTitleForAllTypes() {
        mediaAssertThat((program().withTitles().build())).hasTitle(TextualType.MAIN, TextualType.SHORT);
    }

    @Test(expected = AssertionError.class)
    public void testHasTitleOnMissingType() {
        mediaAssertThat((program().withTitles().build())).hasTitle(TextualType.ORIGINAL);
    }

    @Test
    public void testHasOnlyTitle() {
        mediaAssertThat((program().withTitles().build())).hasOnlyTitles(OwnerType.BROADCASTER, OwnerType.MIS);
    }

    @Test(expected = AssertionError.class)
    public void testHasOnlyTitleWithFailingOwner() {
        mediaAssertThat((program().withTitles().build())).hasOnlyTitles(OwnerType.BROADCASTER);
    }

    @Test(expected = AssertionError.class)
    public void testHasDescriptionOnNull() {
        mediaAssertThat((MediaObject)null).hasDescriptions();
    }

    @Test(expected = AssertionError.class)
    public void testHasDescriptionWhenEmpty() {
        mediaAssertThat(program().build()).hasDescriptions();
    }

    @Test
    public void testHasDescription() {
        mediaAssertThat(program().withDescriptions().build()).hasDescriptions();
    }

    @Test
    public void testHasDescriptionWithOwnerAndType() {
        mediaAssertThat(program().withDescriptions().build()).hasDescription(OwnerType.MIS, TextualType.MAIN);
    }

    @Test(expected = AssertionError.class)
    public void testHasDescriptionForTextAndTypeOnFailure() {
        mediaAssertThat((program().withDescriptions().build())).hasDescription("Main description", TextualType.SUB);
    }

    public void testHasDescriptionForTextAndType() {
        mediaAssertThat((program().withDescriptions().build())).hasDescription("Main description", TextualType.MAIN);
    }

    @Test(expected = AssertionError.class)
    public void testHasDescriptionForTextAndOwnerOnFailure() {
        mediaAssertThat((program().withDescriptions().build())).hasDescription("Main description", OwnerType.MIS);
    }

    public void testHasDescriptionForTextAndOwner() {
        mediaAssertThat((program().withDescriptions().build())).hasDescription("Main description", OwnerType.BROADCASTER);
    }

    @Test
    public void testHasDescriptionForAllOwners() {
        mediaAssertThat((program().withDescriptions().build())).hasDescription(OwnerType.BROADCASTER, OwnerType.MIS);
    }

    @Test(expected = AssertionError.class)
    public void testHasDescriptionOnMissingOwner() {
        mediaAssertThat((program().withDescriptions().build())).hasDescription(OwnerType.CERES);
    }

    @Test
    public void testHasDescriptionForAllTypes() {
        mediaAssertThat((program().withDescriptions().build())).hasDescription(TextualType.MAIN, TextualType.SHORT);
    }

    @Test(expected = AssertionError.class)
    public void testHasDescriptionOnMissingType() {
        mediaAssertThat((program().withDescriptions().build())).hasDescription(TextualType.ORIGINAL);
    }

    @Test
    public void testHasOnlyDescriptionWithOwner() {
        mediaAssertThat(program().withDescriptions().build()).hasOnlyDescriptions(OwnerType.MIS, OwnerType.BROADCASTER);
    }

    @Test(expected = AssertionError.class)
    public void testHasOnlyDescriptionWithFailingOwner() {
        mediaAssertThat(program().withDescriptions().build()).hasOnlyDescriptions(OwnerType.MIS);
    }

    @Test(expected = AssertionError.class)
    public void testHasPredictionsOnFailure() {
        mediaAssertThat(program().build()).hasPredictions();
    }

    @Test
    public void testHasPredictionsOnPlatform() {
        mediaAssertThat(program().withPredictions().build()).hasPredictions(Platform.TVVOD, Platform.INTERNETVOD);
    }

    @Test(expected = AssertionError.class)
    public void testHasOnlyPredictionsOnFailure() {
        mediaAssertThat(program().withPredictions().build()).hasOnlyPredictions(Platform.TVVOD);
    }

    @Test
    public void testHasOnlyPredictions() {
        mediaAssertThat(program().withPredictions().build()).hasOnlyPredictions(Platform.TVVOD, Platform.INTERNETVOD);
    }

    @Test
    public void testHasPredictionOnPlatformAndState() {
        mediaAssertThat(program().withPredictions().build()).hasPrediction(Platform.INTERNETVOD, Prediction.State.REVOKED);
    }

    @Test
    public void testHasLocation() {
        mediaAssertThat(program().withLocations().build()).hasLocations();
    }

    @Test
    public void testHasLocationForAllOwners() {
        mediaAssertThat((program().withLocations().build())).hasLocation(OwnerType.BROADCASTER, OwnerType.NEBO);
    }

    @Test(expected = AssertionError.class)
    public void testHasLocationOnMissingOwner() {
        mediaAssertThat((program().withLocations().build())).hasLocation(OwnerType.CERES);
    }

    @Test
    public void testHasOnlyLocationForAllOwners() {
        mediaAssertThat((program().withLocations().build())).hasOnlyLocation(OwnerType.BROADCASTER, OwnerType.NEBO);
    }

    @Test(expected = AssertionError.class)
    public void testHasOnlyLocationForFailingOwners() {
        mediaAssertThat((program().withLocations().build())).hasOnlyLocation(OwnerType.BROADCASTER);
    }

    @Test
    public void testHasLocationWithUrl() {
        mediaAssertThat((program().withLocations().build())).hasLocations("http://player.omroep.nl/?aflID=4393288", "http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf");
    }

    @Test(expected = AssertionError.class)
    public void testHasLocationOnMissingUrl() {
        mediaAssertThat((program().withLocations().build())).hasLocations("http:missing");
    }

    @Test(expected = AssertionError.class)
    public void testHasLocationWithRestrictionOnNull() {
        mediaAssertThat((MediaObject)null).hasLocationWithRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testHasLocationWithRestrictionWhenMissing() {
        mediaAssertThat((program().withLocations().build())).hasLocationWithRestriction();
    }

    @Test
    public void testHasLocationWithRestriction() {
        Program program = program().withLocations().build();
        program.getLocations().first().setPublishStartInstant(Instant.now());
        mediaAssertThat(program).hasLocationWithRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testHasLocationWithRestrictionOnlyOnNull() {
        mediaAssertThat((MediaObject)null).hasOnlyLocationsWithRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testHasLocationWithRestrictionOnlyWhenMissing() {
        mediaAssertThat((program().withLocations().build())).hasOnlyLocationsWithRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testHasLocationWithRestrictionOnlyWhenNotAllSet() {
        Program program = program().withLocations().build();
        program.getLocations().first().setPublishStartInstant(Instant.now());
        mediaAssertThat(program).hasOnlyLocationsWithRestriction();
    }

    @Test
    public void testHasLocationWithRestrictionOnly() {
        Program program = program().withLocations().build();
        for(Location location : program.getLocations()) {
            location.setPublishStartInstant(Instant.now());
        }
        mediaAssertThat(program).hasOnlyLocationsWithRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testHasPublicationWindowOnNull() {
        mediaAssertThat((MediaObject)null).hasPublicationWindow();
    }

    @Test(expected = AssertionError.class)
    public void testHasPublicationWindow() {
        mediaAssertThat(program().build()).hasPublicationWindow();
    }

    @Test
    public void testHasPublicationWindowOnStart() {
        mediaAssertThat(program().withPublishStart().build()).hasPublicationWindow();
    }

    @Test
    public void testHasPublicationWindowOnStop() {
        mediaAssertThat(program().withPublishStop().build()).hasPublicationWindow();
    }

    @Test(expected = AssertionError.class)
    public void testHasPortalRestrictionOnNull() {
        mediaAssertThat((MediaObject)null).hasPortalRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testHasPortalRestrictionWhenEmpty() {
        mediaAssertThat(program().build()).hasPortalRestriction();
    }

    @Test
    public void testHasPortalRestriction() {
        mediaAssertThat(program().withPortalRestrictions().build()).hasPortalRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testHasGeoRestrictionOnNull() {
        mediaAssertThat((MediaObject)null).hasGeoRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testHasGeoRestrictionWhenEmpty() {
        mediaAssertThat(program().build()).hasGeoRestriction();
    }

    @Test
    public void testHasGeoRestriction() {
        mediaAssertThat(program().withGeoRestrictions().build()).hasGeoRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testIsRestrictedOnNull() {
        mediaAssertThat((MediaObject)null).isRestricted();
    }

    @Test(expected = AssertionError.class)
    public void testIsRestrictedWhenNotRestricted() {
        mediaAssertThat(program().build()).isRestricted();
    }

    @Test
    public void testIsRestrictedWithPublishStart() {
        mediaAssertThat(program().withPublishStart().build()).isRestricted();
    }

    @Test
    public void testIsRestrictedWithPortalRestriction() {
        mediaAssertThat(program().withPortalRestrictions().build()).isRestricted();
    }

    @Test
    public void testIsRestrictedWithGeoRestriction() {
        mediaAssertThat(program().withGeoRestrictions().build()).isRestricted();
    }

    @Test
    public void testIsRestrictedWithRestrictedLocations() {
        Program program = program().withLocations().build();
        for(Location location : program.getLocations()) {
            location.setPublishStartInstant(Instant.now());
        }
        mediaAssertThat(program).isRestricted();
    }

    @Test(expected = AssertionError.class)
    public void testHasRelationsOnNull() {
        mediaAssertThat((MediaObject)null).hasRelations();
    }

    @Test(expected = AssertionError.class)
    public void testHasRelationsWhenEmpty() {
        mediaAssertThat(program().build()).hasRelations();
    }

    @Test
    public void testHasRelations() {
        mediaAssertThat(program().withRelations().build()).hasRelations();
    }

    @Test(expected = AssertionError.class)
    public void testHasRelationWhenMissing() {
        mediaAssertThat(program().withRelations().build()).hasRelation(new Relation(new RelationDefinition("LABEL", "AVRO")));
    }

    @Test
    public void testHasRelation() {
        mediaAssertThat(program().withRelations().build()).hasRelation(new Relation(new RelationDefinition("LABEL", "VPRO"), "http://www.bluenote.com/", "Blue Note"));
    }
/*
    @Test
    public void testHasCeresRecord() throws Exception {
        Location location = new Location("http://bla/", OwnerType.BROADCASTER);
        Program program = program().locations(location).id(1L).build();

        LocationAuthorityRecord.authoritative(program, Platform.INTERNETVOD);
        mediaAssertThat(program).hasCeresRecord(Platform.INTERNETVOD);
    }

    @Test(expected = AssertionError.class)
    public void testHasCeresRecordForOtherPlatform() throws Exception {
        Location location = new Location("http://bla/", OwnerType.BROADCASTER);
        Program program = program().locations(location).id(1L).build();
        LocationAuthorityRecord.authoritative(program, Platform.INTERNETVOD);
        mediaAssertThat(program).hasCeresRecord(Platform.PLUSVOD);
    }

    @Test(expected = AssertionError.class)
    public void testHasCeresRecordWhenFalse() throws Exception {
        mediaAssertThat(program().build()).hasCeresRecord(Platform.INTERNETVOD);
    }*/
}
